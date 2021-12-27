package com.jd.platform.jlog.worker.disruptor;

import cn.hutool.core.date.DateUtil;
import com.jd.platform.jlog.common.model.RunLogMessage;
import com.jd.platform.jlog.common.model.TracerBean;
import com.jd.platform.jlog.common.model.TracerData;
import com.jd.platform.jlog.common.utils.FastJsonUtils;
import com.jd.platform.jlog.common.utils.ProtostuffUtils;
import com.jd.platform.jlog.common.utils.ZstdUtils;
import com.jd.platform.jlog.worker.store.TracerLogToDbStore;
import com.jd.platform.jlog.worker.store.TracerModelToDbStore;
import com.lmax.disruptor.WorkHandler;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

/**
 * TracerConsumer
 *
 * @author wuweifeng
 * @version 1.0
 * @date 2021-08-24
 */
public class TracerConsumer implements WorkHandler<OneTracer> {
    /**
     * 已消费完毕的总数量
     */
    private static final LongAdder totalDealCount = new LongAdder();
    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 待入库队列，出入参model
     */
    private TracerModelToDbStore tracerModelToDbStore;
    /**
     * 待入库队列，普通日志
     */
    private TracerLogToDbStore tracerLogToDbStore;

    public TracerConsumer(TracerModelToDbStore tracerModelToDbStore, TracerLogToDbStore tracerLogToDbStore) {
        this.tracerModelToDbStore = tracerModelToDbStore;
        this.tracerLogToDbStore = tracerLogToDbStore;
    }

    @Override
    public void onEvent(OneTracer oneTracer) {
        try {
            long totalConsume = totalDealCount.longValue();
            boolean needInfo = totalConsume % 1000 == 0;

            //压缩后的字节数组
            byte[] decompressBytes = ZstdUtils.decompressBytes(oneTracer.getBytes());

            TracerData tracerData = ProtostuffUtils.deserialize(decompressBytes, TracerData.class);
            //包含了多个tracer对象
            List<TracerBean> tracerBeanList = tracerData.getTracerBeanList();
            buildTracerModel(tracerBeanList);

            //处理完毕，将数量加1
            totalDealCount.increment();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建要入库的对象
     */
    private void buildTracerModel(List<TracerBean> tracerBeanList) {
        //遍历传过来的
        for (TracerBean tracerBean : tracerBeanList) {
            //普通日志
            if ("-99999".equals(tracerBean.getTracerId())) {
                dealTracerLog(tracerBean);

            } else {
                dealFilterModel(tracerBean);
            }

        }
    }

    /**
     * 处理中途日志
     */
    private void dealTracerLog(TracerBean tracerBean) {
        List<Map<String, Object>> mapList = tracerBean.getTracerObject();
        Map<String, Object> objectMap = mapList.get(0);
        //遍历value集合，里面每个都是一个RunLogMessage对象
        for (Object object :objectMap.values()) {
            Map<String, Object> map = new HashMap<>();

            RunLogMessage runLogMessage = (RunLogMessage) object;
            map.put("tracerId", runLogMessage.getTracerId());
            map.put("className", runLogMessage.getClassName());
            map.put("threadName", runLogMessage.getThreadName());
            map.put("methodName", runLogMessage.getMethodName());
            map.put("logLevel", runLogMessage.getLogLevel());
            map.put("createTime", DateUtil.formatDateTime(new Date(runLogMessage.getCreateTime())));
            map.put("content", runLogMessage.getContent());
            tracerLogToDbStore.offer(map);
        }

    }

    /**
     * 处理filter里处理的出入参
     */
    private void dealFilterModel(TracerBean tracerBean) {
        List<Map<String, Object>> mapList = tracerBean.getTracerObject();
        Map<String, Object> requestMap = mapList.get(0);

        long tracerId = requestMap.get("tracerId") == null ? 0 : Long.valueOf(requestMap.get("tracerId").toString());
        //filter的出入参
        Map<String, Object> responseMap = mapList.get(mapList.size() - 1);

        byte[] responseBytes = "default".getBytes();
        if (responseMap.get("response") != null) {
            responseBytes = (byte[]) responseMap.get("response");
        }

        Map<String, Object> map = new HashMap<>();
        //jsf的是用户自己设置的request入参，http的是从httpRequest读取的
        if (requestMap.get("wholeRequest") == null) {
            map.put("requestContent", FastJsonUtils.collectToString(requestMap));
        } else {
            map.put("requestContent", requestMap.get("wholeRequest"));
        }

        //此处做了一个base64编码，否则原编码直接进去，取出来后是String，直接getBytes后无法用Zstd解压
        map.put("responseContent", responseBytes);
        map.put("createTime", DateUtil.formatDateTime(new Date(tracerBean.getCreateTime())));
        map.put("costTime", tracerBean.getCostTime());


        map.put("tracerId", tracerId);

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

        map.put("intoDbTime", DateUtil.formatDateTime(new Date(tracerBean.getCreateTime())));

        tracerModelToDbStore.offer(map);
    }

}
