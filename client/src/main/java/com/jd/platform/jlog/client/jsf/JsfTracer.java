package com.jd.platform.jlog.client.jsf;

import com.jd.platform.jlog.client.Context;
import com.jd.platform.jlog.client.tracerholder.TracerHolder;
import com.jd.platform.jlog.client.udp.UdpSender;
import com.jd.platform.jlog.common.model.TracerBean;
import com.jd.platform.jlog.common.utils.IdWorker;
import com.jd.platform.jlog.common.utils.IpUtils;
import com.jd.platform.jlog.common.utils.ZstdUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * JsfTracer工具类
 *
 * @author wuweifeng
 * @version 1.0
 * @date 2021-10-26
 */
public class JsfTracer {

    /**
     * 临时保存入参
     */
    private static Map<Long, TracerBean> TEMP_HOLDER = new HashMap<>(128);

    /**
     * 跟踪开启
     */
    public static void begin(JsfRequest jsfRequest) {
        long tracerId = IdWorker.nextId();
        TracerHolder.setTracerId(tracerId);

        TracerBean tracerBean = new TracerBean();
        tracerBean.setCreateTime(System.currentTimeMillis());

        List<Map<String, Object>> tracerObject = new ArrayList<>();
        tracerBean.setTracerObject(tracerObject);

        //将request信息保存
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("appName", Context.APP_NAME);
        requestMap.put("serverIp", IpUtils.getIp());

        requestMap.put("tracerId", tracerId);
        requestMap.put("uri", jsfRequest.uri().trim());
        //将用户整个request都放进去
        requestMap.put("wholeRequest", jsfRequest.requestContent());

        requestMap.put("pin", jsfRequest.pin());
        requestMap.put("uuid", jsfRequest.uuid());
        requestMap.put("userIp", jsfRequest.userIp());

        tracerObject.add(requestMap);

        tracerBean.setTracerId(tracerId + "");

        TEMP_HOLDER.put(tracerId, tracerBean);
    }

    /**
     * 记录请求出入参，发送到worker
     */
    public static void end(String response) {
        long tracerId = TracerHolder.getTracerId();
        TracerBean tracerBean = TEMP_HOLDER.get(tracerId);
        if (tracerBean == null) {
            return;
        }

        if (response != null) {
            //最终的要发往worker的response，经历了base64压缩
            byte[] bytes = ZstdUtils.compress(response.getBytes(StandardCharsets.UTF_8));
            byte[] base64Bytes = Base64.getEncoder().encode(bytes);
            Map<String, Object> responseMap = new HashMap<>(8);
            responseMap.put("response", base64Bytes);

            List<Map<String, Object>> tracerObject = tracerBean.getTracerObject();
            tracerObject.add(responseMap);
        }

        //设置耗时
        tracerBean.setCostTime((int) (System.currentTimeMillis() - tracerBean.getCreateTime()));
        UdpSender.offerBean(tracerBean);

        //从缓存删除它
        TEMP_HOLDER.remove(tracerId);
    }

}
