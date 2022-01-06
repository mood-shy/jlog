package com.jd.platform.jlog.clientdemo.consumer;

import cn.hutool.core.util.StrUtil;
import com.jd.platform.jlog.client.etcd.EtcdConfigFactory;
import com.jd.platform.jlog.clientdemo.config.UserConstant;
import com.jd.platform.jlog.common.config.IConfigCenter;
import com.jd.platform.jlog.common.consumer.TracerConsumerExt;
import com.jd.platform.jlog.common.model.TracerBean;
import com.jd.platform.jlog.common.utils.FastJsonUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 入库处理--入参
 * @author shenkaiwen5
 * @version 1.0
 * @date 2022-01-06
 */
@Component
public class ReqConsumerExtImpl implements TracerConsumerExt {

    /**
     * 默认抓取的字段
     */
    private static final List<String> INSERT_DB_FIELD = new ArrayList<String>(){{
        add("pin");
    }};

    @Override
    public boolean dealFilterModelMap(TracerBean tracerBean, Map<String, Object> map) {
        try {
            List<Map<String, Object>> mapList = tracerBean.getTracerObject();
            Map<String, Object> requestMap = mapList.get(0);

            if (requestMap.get("wholeRequest") == null) {
                map.put("requestContent", FastJsonUtils.collectToString(requestMap));
            } else {
                map.put("requestContent", requestMap.get("wholeRequest"));
            }
            //需要入库查询的索引字段（用户定制）
            List<String> fields = INSERT_DB_FIELD;

            IConfigCenter configCenter = EtcdConfigFactory.configCenter();
            String rawField = configCenter.get(UserConstant.INSERT_DB_FIELD);
            if (!StrUtil.isEmpty(rawField)) {
                fields = Arrays.asList(rawField.split(";"));
            }
            //用户定制参数入库
            for (String field: fields) {
                map.put(field, requestMap.get(field));
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
