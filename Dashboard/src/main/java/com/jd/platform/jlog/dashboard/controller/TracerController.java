package com.jd.platform.jlog.dashboard.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.platform.jlog.dashboard.entity.TracerListVO;
import com.jd.platform.jlog.dashboard.entity.TracerVO;
import com.jd.platform.jlog.dashboard.model.QueryListModel;
import com.jd.platform.jlog.dashboard.service.TracerService;
import com.jd.platform.jlog.dashboard.utils.DateUtils;
import com.jd.platform.jlog.dashboard.utils.ResultHelper;
import com.jd.platform.jlog.dashboard.utils.ZstdUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author shenkaiwen5
 * @version 1.0
 * @date 2021-09-01
 */
@Controller
@RequestMapping("tracer/index")
public class TracerController {
    /**
     * 访问前缀
     */
    private static final String prefix = "tracer/index";
    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(TracerController.class);
    /**
     * 查询服务
     */
    @Resource
    private TracerService tracerService;

    /**
     * 主页
     */
    @RequestMapping()
    public String index() {
        return prefix;
    }

    /**
     * 详情页
     */
    @GetMapping("/detail")
    public String detail(@RequestParam("id") String traceId,
                         @RequestParam("uid") String uid,
                         @RequestParam("createTime") String createTime,
                         ModelMap mmap) throws Exception {

        //加synchronized，保证Calendar不出错
        String beginTime = DateUtils.addAndSubtractTime(createTime, -5000L);
        String endTime = DateUtils.addAndSubtractTime(createTime, 5000L);
        //查询数据
        Map<String, Object> map = tracerService.findOne(traceId, uid, beginTime, endTime);

        //转化其中被压缩的response
        String response = map.get("responseContent").toString();
        String resp = ZstdUtils.decompress(response.getBytes(StandardCharsets.ISO_8859_1));
        map.put("responseContent", resp);

        //转化其中被压缩的body
        try {
            String req = map.get("requestContent").toString();
            String re = ZstdUtils.decompress(req.getBytes(StandardCharsets.ISO_8859_1));
            map.put("requestContent", re);
        } catch (Exception e) {
            logger.info("TracerController.detail", e);
        }

        //转为结果类
        TracerVO tracerVO = new TracerVO();
        BeanUtils.populate(tracerVO, map);
        //存入返回模板值
        mmap.put("tracerVO", tracerVO);
        //logger.info(new String(zstd));

        return "tracer/detail";
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public TracerListVO list(QueryListModel queryListModel) {
        //这里下发的不一定是完整数据，可能缺几个属性，完整的是查询单个返回TracerVO
        TracerListVO listVO = tracerService.list(queryListModel);
        if (listVO == null) {
            return ResultHelper.fail();
        }
        //处理时间格式，这存的时间拿map接收无法@JsonFormat
        List<Map<String, Object>> rows = listVO.getRows();
        for (Map<String, Object> map : rows) {
            //处理createTime
            String oldTime = map.get("createTime").toString();
            map.put("createTime", oldTime
                    .replace("T", " ")
                    .substring(0, oldTime.length() - 2)
            );
            //处理tracerId
            String tracerId = map.get("tracerId").toString();
            map.put("tracerId", tracerId);
        }

        return ResultHelper.success(listVO);
    }


    @RequestMapping("/link")
    public String queryLink(@RequestParam("id") String tracerId,
                            @RequestParam("createTime") String createTime,
                            ModelMap mmap) {
        mmap.put("id", tracerId);
        mmap.put("createTime", createTime);
        return "tracer/link";
    }

    /**
     * 链路日志
     */
    @RequestMapping("/logList")
    @ResponseBody
    public TracerListVO list(@RequestParam("id") long tracerId,
                             @RequestParam("createTime") String createTime,
                             @RequestParam("pageNum")long pageNum) {

        TracerListVO listVO = tracerService.findLogByTracerId(tracerId, createTime, pageNum);
        if (listVO == null) {
            return ResultHelper.fail();
        }
        //处理时间格式，这存的时间拿map接收无法@JsonFormat
        List<Map<String, Object>> rows = listVO.getRows();
        for (Map<String, Object> map : rows) {
            //处理createTime
            String oldTime = map.get("createTime").toString();
            map.put("createTime", oldTime
                    .replace("T", " ")
                    .substring(0, oldTime.length() - 2)
            );
        }

        return ResultHelper.success(listVO);
    }


}
