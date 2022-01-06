package com.jd.platform.jlog.clientdemo.consumer;

import com.jd.platform.jlog.common.consumer.TracerConsumerExt;
import com.jd.platform.jlog.common.model.TracerBean;
import com.jd.platform.jlog.common.utils.FastJsonUtils;
import io.netty.util.internal.StringUtil;
import org.springframework.stereotype.Component;

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

    @Override
    public boolean dealFilterModelMap(TracerBean tracerBean, Map<String, Object> map) {
        try {
            List<Map<String, Object>> mapList = tracerBean.getTracerObject();
            Map<String, Object> requestMap = mapList.get(0);

            long tracerId = requestMap.get("tracerId") == null ? 0 : Long.valueOf(requestMap.get("tracerId").toString());
            map.put("tracerId", tracerId);

            if (requestMap.get("wholeRequest") == null) {
                map.put("requestContent", FastJsonUtils.collectToString(requestMap));
            } else {
                map.put("requestContent", requestMap.get("wholeRequest"));
            }

            String pin = requestMap.get("pin") == null ? "" : requestMap.get("pin").toString();
            map.put("pin", pin);

            String uri = requestMap.get("uri") == null ? "" : requestMap.get("uri").toString();
            map.put("uri", uri);

            //appName
            String appName = requestMap.get("appName") == null ? "" : requestMap.get("appName").toString();
            map.put("appName", appName);

            String openudid = requestMap.get("openudid") == null ? "" : requestMap.get("openudid").toString();

            if (StringUtil.isNullOrEmpty(openudid)) {
                String uuid = requestMap.get("uuid") == null ? "" : requestMap.get("uuid").toString();
                map.put("uuid", uuid);
            } else {
                map.put("uuid", openudid);
            }

            String client = requestMap.get("client") == null ? "" : requestMap.get("client").toString();
            int clientType = 0;
            if ("apple".equals(client)) {
                clientType = 2;
            } else if ("android".equals(client)) {
                clientType = 1;
            }
            map.put("clientType", clientType);
            String clientVersion = requestMap.get("clientVersion") == null ? "" : requestMap.get("clientVersion").toString();
            map.put("clientVersion", clientVersion);

            String userIp = requestMap.get("ip") == null ? "" : requestMap.get("ip").toString();
            map.put("userIp", userIp);
            String serverIp = requestMap.get("serverIp") == null ? "" : requestMap.get("serverIp").toString();
            map.put("serverIp", serverIp);

        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
