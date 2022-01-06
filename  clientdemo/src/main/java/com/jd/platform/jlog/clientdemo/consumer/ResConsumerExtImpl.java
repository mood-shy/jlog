package com.jd.platform.jlog.clientdemo.consumer;

import com.jd.platform.jlog.common.consumer.TracerConsumerExt;
import com.jd.platform.jlog.common.model.TracerBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 入库处理--出参
 * @author shenkaiwen5
 * @version 1.0
 * @date 2022-01-06
 */
@Component
public class ResConsumerExtImpl implements TracerConsumerExt {
    @Override
    public boolean dealFilterModelMap(TracerBean tracerBean, Map<String, Object> map) {
        try {
            //filter的出入参
            List<Map<String, Object>> mapList = tracerBean.getTracerObject();
            Map<String, Object> responseMap = mapList.get(mapList.size() - 1);

            byte[] responseBytes = "default".getBytes();
            if (responseMap.get("response") != null) {
                responseBytes = (byte[]) responseMap.get("response");
            }

            //此处做了一个base64编码，否则原编码直接进去，取出来后是String，直接getBytes后无法用Zstd解压
            map.put("responseContent", responseBytes);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
