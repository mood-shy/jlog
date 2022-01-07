package com.jd.platform.jlog.clientdemo.consumer;

import cn.hutool.core.date.DateUtil;
import com.jd.platform.jlog.common.consumer.TracerConsumerExt;
import com.jd.platform.jlog.common.model.TracerBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 入库处理--通用
 * @author shenkaiwen5
 * @version 1.0
 * @date 2022-01-06
 */
@Component
public class CommonConsumerExtImpl implements TracerConsumerExt {
    @Override
    public boolean dealFilterModelMap(TracerBean tracerBean, Map<String, Object> map) {
        try {
            List<Map<String, Object>> mapList = tracerBean.getTracerObject();
            Map<String, Object> requestMap = mapList.get(0);

            //tracerId
            long tracerId = requestMap.get("tracerId") == null ? 0 : Long.valueOf(requestMap.get("tracerId").toString());
            map.put("tracerId", tracerId);
            //uri
            String uri = requestMap.get("uri") == null ? "" : requestMap.get("uri").toString();
            map.put("uri", uri);
            //ip
            String userIp = requestMap.get("ip") == null ? "" : requestMap.get("ip").toString();
            map.put("userIp", userIp);
            String serverIp = requestMap.get("serverIp") == null ? "" : requestMap.get("serverIp").toString();
            map.put("serverIp", serverIp);
            //time
            map.put("createTime", DateUtil.formatDateTime(new Date(tracerBean.getCreateTime())));
            map.put("costTime", tracerBean.getCostTime());
            map.put("intoDbTime", DateUtil.formatDateTime(new Date()));
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
