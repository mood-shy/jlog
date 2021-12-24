package com.jd.platform.jlog.worker.entity;


import java.io.Serializable;

/**
 * 入库、检索时用的对象
 *
 * @author wuweifeng
 * @version 1.0
 * @date 2021-08-20
 */
public class TracerModel implements Serializable {
    /**
     * tracerId
     */
    private long tracerId;
    /**
     * 请求的入参
     */
    private String requestContent;
    /**
     * 响应的出参
     */
    private String responseContent;
    /**
     * 日志请求时间（数据库里存的是DateTime，2021-08-24 19:47:30.0）
     */
    private long createTime;
    /**
     * 请求耗时（即响应时间戳减去请求时间戳）
     */
    private int costTime;
    /**
     * 用户pin
     */
    private String pin;
    /**
     * 用户uuid
     */
    private String uuid;
    /**
     * Android=1、ios=2
     */
    private int clientType;
    /**
     * 客户端版本号，9.3.6
     */
    private String clientVersion;
    /**
     * 用户ip
     */
    private String userIp;
    /**
     * 服务端ip
     */
    private String serverIp;
    /**
     * 入库时间
     */
    private long intoDbTime;


    public long getIntoDbTime() {
        return intoDbTime;
    }

    public void setIntoDbTime(long intoDbTime) {
        this.intoDbTime = intoDbTime;
    }

    public String getRequestContent() {
        return requestContent;
    }

    public void setRequestContent(String requestContent) {
        this.requestContent = requestContent;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public long getTracerId() {
        return tracerId;
    }

    public void setTracerId(long tracerId) {
        this.tracerId = tracerId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getCostTime() {
        return costTime;
    }

    public void setCostTime(int costTime) {
        this.costTime = costTime;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }


    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }
}
