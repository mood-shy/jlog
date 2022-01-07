package com.jd.platform.jlog.clientdemo.web;

import com.jd.platform.jlog.client.udp.UdpSender;
import com.jd.platform.jlog.common.model.TracerBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shenkaiwen5
 * @version 1.0
 * @date 2021-12-27
 */
@RestController
public class TestController {

    /**
     * do nothing
     * just as an adapter for this project common log helper
     *
     */
    private static Logger RequestLog = LoggerFactory.getLogger("RequestLog");

    @RequestMapping("/index")
    public Object index() {
        TracerBean tracerBean = new TracerBean();
        tracerBean.setTracerId("11111");

        RequestLog.info("哈哈哈哈哈哈");

        return tracerBean;
    }

}
