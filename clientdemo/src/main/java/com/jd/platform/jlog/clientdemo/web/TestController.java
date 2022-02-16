package com.jd.platform.jlog.clientdemo.web;

import com.alibaba.nacos.api.exception.NacosException;
import com.jd.platform.jlog.common.config.ConfigCenterEnum;
import com.jd.platform.jlog.common.config.ConfigCenterFactory;
import com.jd.platform.jlog.common.config.IConfigCenter;
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
    public Object index() throws NacosException {
        TracerBean tracerBean = new TracerBean();
        tracerBean.setTracerId("11111");

        IConfigCenter client = ConfigCenterFactory.getClient(ConfigCenterEnum.ETCD);
        try{
            client.put("/test","val1");
        }catch (Exception e){
            e.printStackTrace();
        }

        String val = ConfigCenterFactory.getClient(ConfigCenterEnum.ETCD).get("/test");
        System.out.println("val ===>   "+val);
        RequestLog.info("哈哈哈哈哈哈");

        return tracerBean;
    }

    @RequestMapping("/log")
    public Object log() throws NacosException {
        RequestLog.info("|tag3=val3||tag4=val4||这是随便的log|");
        return 1;
    }

}
