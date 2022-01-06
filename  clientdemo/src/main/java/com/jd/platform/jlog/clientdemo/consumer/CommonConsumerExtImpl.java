package com.jd.platform.jlog.clientdemo.consumer;

import cn.hutool.core.date.DateUtil;
import com.jd.platform.jlog.common.consumer.TracerConsumerExt;
import com.jd.platform.jlog.common.model.TracerBean;
import org.springframework.stereotype.Component;

import java.util.Date;
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
            map.put("createTime", DateUtil.formatDateTime(new Date(tracerBean.getCreateTime())));
            map.put("costTime", tracerBean.getCostTime());
            map.put("intoDbTime", DateUtil.formatDateTime(new Date()));
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
