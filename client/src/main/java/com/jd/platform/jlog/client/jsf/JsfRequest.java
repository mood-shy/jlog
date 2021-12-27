package com.jd.platform.jlog.client.jsf;


/**
 * jsf请求入参包装对象
 * @author wuweifeng
 * @version 1.0
 * @date 2021-10-26
 */
public interface JsfRequest {
    /**
     * 请求的完整入参
     */
    String requestContent();

    /**
     * 接口名
     */
    String uri();

    /**
     * 用户pin
     */
    default String pin() {
        return "";
    }

    /**
     * 用户uuid
     */
    default String uuid() {
        return "";
    }

    /**
     * 用户ip
     */
    default String userIp() {
        return "";
    }
}
