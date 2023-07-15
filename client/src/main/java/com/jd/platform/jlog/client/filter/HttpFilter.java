package com.jd.platform.jlog.client.filter;

import com.jd.platform.jlog.client.Context;
import com.jd.platform.jlog.client.cache.ExtParamFactory;
import com.jd.platform.jlog.client.percent.DefaultTracerPercentImpl;
import com.jd.platform.jlog.client.percent.ITracerPercent;
import com.jd.platform.jlog.client.tracerholder.TracerHolder;
import com.jd.platform.jlog.client.udp.UdpSender;
import com.jd.platform.jlog.common.model.TracerBean;
import com.jd.platform.jlog.common.handler.CompressHandler.Outcome;
import com.jd.platform.jlog.common.utils.IdWorker;
import com.jd.platform.jlog.common.utils.IpUtils;
import com.jd.platform.jlog.common.utils.StringUtils;
import com.jd.platform.jlog.core.ClientHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


/**
 * HttpFilter
 * http://blog.chinaunix.net/uid-20783755-id-4729930.html
 *
 * @author wuweifeng
 * @version 1.0
 * @date 2021-08-16
 */
public class HttpFilter implements Filter {
    /**
     * 获取切量百分比的
     */
    private ITracerPercent iTracerPercent;
    private Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * 传入百分比实现类
     */
    public HttpFilter(ITracerPercent iTracerPercent) {
        this.iTracerPercent = iTracerPercent;
    }

    public HttpFilter() {
        iTracerPercent = new DefaultTracerPercentImpl();
    }

    public void setTracerPercent(ITracerPercent iTracerPercent) {
        this.iTracerPercent = iTracerPercent;
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        RequestWrapper requestWrapper = new RequestWrapper((HttpServletRequest) servletRequest);
        long currentTImeMills = System.currentTimeMillis();
        String uri = requestWrapper.getRequestURI().replace("/", "");
        //设置随机数
        Random random = new Random(currentTImeMills);
        //1-100之间
        int number = random.nextInt(100) + 1;
        //此处要有个开关，控制百分比
        if (iTracerPercent.percent() < number) {
            filterChain.doFilter(requestWrapper, servletResponse);
            return;
        }
        //如果是要忽略的接口，就继续执行，不搜集信息
        if (iTracerPercent.ignoreUriSet() != null && iTracerPercent.ignoreUriSet().contains(uri)) {
            filterChain.doFilter(requestWrapper, servletResponse);
            return;
        }
        //链路唯一Id
        long tracerId = IdWorker.nextId();
        TracerHolder.setTracerId(tracerId);
        TracerBean tracerBean = new TracerBean();
        tracerBean.setTracerId(tracerId);
        tracerBean.setCreateTimeLong(System.currentTimeMillis());
        tracerBean.setUri(uri);
        tracerBean.setApp(Context.APP_NAME);

        //处理request的各个入参
        parseRequestMap(requestWrapper, tracerBean);
        try {
            //处理response
            tracerBean.setResponseContent(dealResponseMap(requestWrapper, servletResponse,
                    resp, filterChain));
        } catch (Exception e) {
            //异常信息
            tracerBean.setErrmsg(StringUtils.errorInfoToString(e));
            filterChain.doFilter(requestWrapper, servletResponse);
        }finally {
            //设置耗时
            tracerBean.setCostTime((System.currentTimeMillis() - tracerBean.getCreateTimeLong()));
            //udp发送
            UdpSender.offerBean(tracerBean);
        }
    }

    /**
     * 处理出参相关信息
     */
    private byte[] dealResponseMap(ServletRequest servletRequest, ServletResponse servletResponse, HttpServletResponse resp,
                                   FilterChain filterChain) throws IOException, ServletException {
        // 包装响应对象 resp 并缓存响应数据
        ResponseWrapper mResp = new ResponseWrapper(resp);
        filterChain.doFilter(servletRequest, mResp);
        byte[] contentBytes = mResp.getContent();
        String content = new String(contentBytes);

        Map<String, Object> map = ExtParamFactory.getRespMap(content);
        Outcome outcome = ClientHandler.processResp(contentBytes, map);

        //此处可以对content做处理,然后再把content写回到输出流中
        servletResponse.setContentLength(-1);
        PrintWriter out = servletResponse.getWriter();
        out.write(content);
        out.flush();
        out.close();

        return (byte[]) outcome.getContent();
    }

    /**
     * 处理入参相关信息
     */
    private void parseRequestMap(RequestWrapper requestWrapper, TracerBean tracerBean)  {
        //request的各个入参
        Map<String, String[]> params = requestWrapper.getParameterMap();
        Map<String, Object> requestMap = new HashMap<>(params.size());
        for (String key : params.keySet()) {
            requestMap.put(key, params.get(key)[0]);
        }
        tracerBean.setUid((String) requestMap.get("uid"));
        // 自定义的其他的参数对
        requestMap.putAll(ExtParamFactory.getReqMap(requestWrapper));
        Outcome out = ClientHandler.processReq(requestMap);
        tracerBean.setRequestContent((byte[]) out.getContent());
    }

    @Override
    public void destroy() {

    }
}
