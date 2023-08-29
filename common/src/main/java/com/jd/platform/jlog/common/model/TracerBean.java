package com.jd.platform.jlog.common.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author xiaochangbai
 * @date 2023-07-15 11:25
 */
public class TracerBean implements Serializable {

    private Long tracerId;
    private byte[] requestContent;
    private byte[] responseContent;
    private Long costTime;
    private String uid;
    private String errno;
    private String errmsg;
    private String app;
    private String uri;
    private String createTime;

    private Long createTimeLong;


    public Long getTracerId() {
        return tracerId;
    }

    public void setTracerId(Long tracerId) {
        this.tracerId = tracerId;
    }

    public byte[] getRequestContent() {
        return requestContent;
    }

    public void setRequestContent(byte[] requestContent) {
        this.requestContent = requestContent;
    }

    public byte[] getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(byte[] responseContent) {
        this.responseContent = responseContent;
    }

    public Long getCostTime() {
        return costTime;
    }

    public void setCostTime(Long costTime) {
        this.costTime = costTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getErrno() {
        return errno;
    }

    public void setErrno(String errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Long getCreateTimeLong() {
        return createTimeLong;
    }

    public void setCreateTimeLong(Long createTimeLong) {
        this.createTimeLong = createTimeLong;
    }

    @Override
    public String toString() {
        return "TracerModel{" +
                "tracerId=" + tracerId +
                ", requestContent=" + Arrays.toString(requestContent) +
                ", responseContent=" + Arrays.toString(responseContent) +
                ", costTime=" + costTime +
                ", uid='" + uid + '\'' +
                ", errno='" + errno + '\'' +
                ", errmsg='" + errmsg + '\'' +
                ", app='" + app + '\'' +
                ", uri='" + uri + '\'' +
                ", createTime=" + createTime +
                ", createTimeLong=" + createTimeLong +
                '}';
    }
}
