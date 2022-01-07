package com.jd.platform.jlog.clientdemo.interceptor;

import com.jd.platform.jlog.common.model.TracerBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 出入参抓取拓展点
 * @author shenkaiwen5
 * @version 1.0
 * @date 2022-01-06
 */
public interface TracerBeanAttrExt {
    /**
     * 处理请求出入参，填充TracerBean
     */
    boolean dealWithReqRes(HttpServletRequest request, HttpServletResponse response, TracerBean tracerBean);
}
