package com.jd.platform.jlog.clientdemo.web;


import com.jd.platform.jlog.common.model.TracerBean;
import com.jd.platform.jlog.core.Configurator;
import com.jd.platform.jlog.core.ConfiguratorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public Object index()  {
        TracerBean tracerBean = new TracerBean();
        tracerBean.setTracerId("11111");

        Configurator configurator = ConfiguratorFactory.getInstance();
        try{
            configurator.putConfig("/test","val1");
        }catch (Exception e){
            e.printStackTrace();
        }

        String val = configurator.getConfig("/test");
        System.out.println("val ===>   "+val);
        RequestLog.info("哈哈哈哈哈哈");

        return tracerBean;
    }

    @RequestMapping("/log")
    public Object log() {
        RequestLog.info("|tag3=val3||tag4=val4||这是随便的log|");
        return 1;
    }


    @PostMapping(value = "/test", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object test(@RequestBody TestReq req) {


        return 1;
    }



}
