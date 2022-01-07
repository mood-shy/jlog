package com.jd.platform.jlog.clientdemo.interceptor.impl;

import com.jd.platform.jlog.client.filter.ResponseWrapper;
import com.jd.platform.jlog.clientdemo.interceptor.TracerBeanAttrExt;
import com.jd.platform.jlog.common.model.TracerBean;
import com.jd.platform.jlog.common.utils.ZstdUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shenkaiwen5
 * @version 1.0
 * @date 2022-01-06
 */
@Component
public class ResponseBeanAttrExtImpl implements TracerBeanAttrExt {
    @Override
    public boolean dealWithReqRes(HttpServletRequest request, HttpServletResponse response, TracerBean tracerBean) {
        try {
            // 包装响应对象 resp 并缓存响应数据
            ResponseWrapper mResp = new ResponseWrapper(response);
            byte[] contentBytes = mResp.getContent();
            String content = new String(contentBytes);
            //最终的要发往worker的response，经历了base64压缩
            byte[] bytes = ZstdUtils.compress(contentBytes);
            byte[] base64Bytes = Base64.getEncoder().encode(bytes);

            //保存至Bean
            Map<String, Object> responseMap = new HashMap<String, Object>(8){{
                put("response", base64Bytes);
            }};
            tracerBean.getTracerObject().add(responseMap);

            //此处可以对content做处理,然后再把content写回到输出流中
            response.setContentLength(-1);
            PrintWriter out = response.getWriter();
            out.write(content);
            out.flush();
            out.close();

        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
