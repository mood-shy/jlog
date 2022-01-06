package com.jd.platform.jlog.clientdemo.interceptor.impl;

import com.jd.platform.jlog.client.Context;
import com.jd.platform.jlog.clientdemo.interceptor.TracerBeanAttrExt;
import com.jd.platform.jlog.common.model.TracerBean;
import com.jd.platform.jlog.common.utils.IpUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 抓取入参
 * @author shenkaiwen5
 * @version 1.0
 * @date 2022-01-06
 */
@Component
public class RequestBeanAttrExtimpl implements TracerBeanAttrExt {
    @Override
    public boolean dealWithReqRes(HttpServletRequest request, HttpServletResponse response, TracerBean tracerBean) {
        try {
            //请求接口
            String uri = request.getRequestURI().replace("/", "");
            //请求的各个入参
            Map<String, String[]> params = request.getParameterMap();

            //request信息保存
            Map<String, Object> requestMap = new HashMap<>(params.size());
            for (String key : params.keySet()) {
                requestMap.put(key, params.get(key)[0]);
            }

            requestMap.put("serverIp", IpUtils.getIp());
            requestMap.put("tracerId", tracerBean.getTracerId());
            requestMap.put("uri", uri);

            tracerBean.getTracerObject().add(requestMap);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
