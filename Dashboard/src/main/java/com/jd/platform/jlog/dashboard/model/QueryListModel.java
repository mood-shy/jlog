package com.jd.platform.jlog.dashboard.model;

import lombok.Data;

/**
 * 查询条件对象
 * @author wuweifeng
 * @version 1.0
 * @date 2021-08-31
 */
@Data
public class QueryListModel {
    /**
     * 追踪事件id
     */
    private Long tracerId;
    /**
     * 用户pin
     */
    private String uid;
    /**
     * 接入应用名
     */
    private String app;

    /**
     * 开始时间
     */
    private String beginTime;
    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 页码
     */
    private Long pageNum;
    /**
     * 接口名
     */
    private String uri;

    private String errno;

    private String errmsg;
}
