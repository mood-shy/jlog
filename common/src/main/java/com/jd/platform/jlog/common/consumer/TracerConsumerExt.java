package com.jd.platform.jlog.common.consumer;

import com.jd.platform.jlog.common.model.TracerBean;

import java.util.Map;

/**
 * 入库属性拓展点
 * @author shenkaiwen5
 * @version 1.0
 * @date 2022-01-06
 */
public interface TracerConsumerExt {
    /**
     * 处理TracerBean至入库map
     */
    boolean dealFilterModelMap(TracerBean tracerBean, Map<String, Object> map);
}
