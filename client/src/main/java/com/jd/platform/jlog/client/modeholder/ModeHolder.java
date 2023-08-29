package com.jd.platform.jlog.client.modeholder;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.jd.platform.jlog.common.constant.SendMode;

/**
 * 线程间传递通讯模式（单播、多播）
 */
public class ModeHolder {
    /**
     * 用于在线程池间也能透传SendMode
     */
    private static TransmittableThreadLocal<SendMode> context = new TransmittableThreadLocal<>();
    /**
     * 设置SendMode到线程里
     */
    public static void setSendMode(SendMode mode) {
        context.set(mode);
    }

    /**
     * 如果没有SendMode，说明没设置上，则返回一个默认值，默认是单播模式
     */
    public static SendMode getSendMode() {
        try {
            return context.get();
        } catch (Exception e) {
            return new SendMode();
        }
    }

}
