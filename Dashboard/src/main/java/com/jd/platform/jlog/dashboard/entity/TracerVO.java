package com.jd.platform.jlog.dashboard.entity;

import lombok.Data;

/**
 * @author shenkaiwen5
 * @version 1.0
 * @date 2021-09-02
 */

public class TracerVO {
    /**
     * 用户pin
     */
    private String uid;
    /**
     * 请求正文
     */
    private String requestContent;
    /**
     * 相应正文
     */
    private Object responseContent;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRequestContent() {
        return requestContent;
    }

    public void setRequestContent(String requestContent) {
        this.requestContent = requestContent;
    }

    public Object getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(Object responseContent) {
        this.responseContent = responseContent;
    }
}
