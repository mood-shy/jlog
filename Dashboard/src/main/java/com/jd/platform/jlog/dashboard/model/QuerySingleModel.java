package com.jd.platform.jlog.dashboard.model;


/**
 * 单个查询条件
 * @author wuweifeng
 * @version 1.0
 * @date 2021-09-01
 */
public class QuerySingleModel {
    /**
     * 追踪事件id
     */
    private Long tracerId;
    /**
     * 用户uid
     */
    private String uid;

    public Long getTracerId() {
        return tracerId;
    }

    public void setTracerId(Long tracerId) {
        this.tracerId = tracerId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
