package com.jd.platform.jlog.worker.disruptor;

import cn.hutool.core.date.DateUtil;
import com.jd.platform.jlog.common.consumer.TracerConsumerExt;
import com.jd.platform.jlog.common.model.RunLogMessage;
import com.jd.platform.jlog.common.model.TracerBean;
import com.jd.platform.jlog.common.model.TracerData;
import com.jd.platform.jlog.common.utils.ProtostuffUtils;
import com.jd.platform.jlog.common.utils.ZstdUtils;
import com.jd.platform.jlog.worker.store.TracerLogToDbStore;
import com.jd.platform.jlog.worker.store.TracerModelToDbStore;
import com.lmax.disruptor.WorkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
@Component
public class TracerConsumer implements WorkHandler<OneTracer> {

    /**
     * 入库处理
     */
    @Resource
    private List<TracerConsumerExt> consumerExtList;

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
        Map<String, Object> map = new HashMap<>(16);

        //处理入库数据
        for (TracerConsumerExt consumerExt: consumerExtList) {
            if (!consumerExt.dealFilterModelMap(tracerBean, map)) {
                break;
            }
        }

        tracerModelToDbStore.offer(map);
    }

}
