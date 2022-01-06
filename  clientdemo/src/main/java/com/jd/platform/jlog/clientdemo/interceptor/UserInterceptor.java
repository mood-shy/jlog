package com.jd.platform.jlog.clientdemo.interceptor;


import com.jd.platform.jlog.client.tracerholder.TracerHolder;
import com.jd.platform.jlog.client.udp.UdpSender;
import com.jd.platform.jlog.clientdemo.config.UserConstant;
import com.jd.platform.jlog.common.config.IConfigCenter;
import com.jd.platform.jlog.common.model.TracerBean;
import com.jd.platform.jlog.common.utils.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Random;

/**
 * 用户拦截器，记录出入参
 * @author shenkaiwen5
 * @version 1.0
 * @date 2022-01-06
 */
@Component
public class UserInterceptor implements HandlerInterceptor {

    /**
     * 配置中心
     */
    @Resource
    private IConfigCenter iConfigCenter;
    /**
     * 出入参记录拓展点
     */
    @Autowired
    private List<TracerBeanAttrExt> tracerBeanAttrExtList;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute(UserConstant.START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        //加一个开关或切量
        if (!tracerPercent()) {
            return;
        }

        //链路唯一Id
        long tracerId = IdWorker.nextId();
        TracerHolder.setTracerId(tracerId);

        //传输对象基础属性设置
        TracerBean tracerBean = new TracerBean((long)request.getAttribute(UserConstant.START_TIME));
        tracerBean.setTracerId(tracerId + "");

        //TODO 可自定义增加拓展点
        for (TracerBeanAttrExt beanAttrExt: tracerBeanAttrExtList) {
            if (!beanAttrExt.dealWithReqRes(request, response, tracerBean)) {
                break;
            }
        }

        UdpSender.offerBean(tracerBean);
    }

    /**
     * 切量，控制是否记录日志
     */
    private boolean tracerPercent() {
        try {
            Integer percent = Integer.valueOf(iConfigCenter.get("tracer.percent"));
            //设置随机数
            Random random = new Random();
            //1-100之间
            int number = random.nextInt(100) + 1;
            if (percent < number) {
                return false;
            }
        } catch (Exception e) {
            return true;
        }
        return true;
    }
}
