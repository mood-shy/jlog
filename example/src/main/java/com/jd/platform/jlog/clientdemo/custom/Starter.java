package com.jd.platform.jlog.clientdemo.custom;

import com.jd.platform.jlog.client.TracerClientStarter;
import com.jd.platform.jlog.client.filter.HttpFilter;
import com.jd.platform.jlog.common.constant.SendMode;
import com.jd.platform.jlog.common.handler.TagConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

/**
 * 启动器 获取配置完成装配和client启动
 * @author shenkaiwen5
 * @version 1.0
 * @date 2021-12-27
 */
@Component
@ConfigurationProperties()
public class Starter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private TagConfig tagConfig ;
    //通讯方式（单播、多播），默认是单播
    private static SendMode sendMode=new SendMode();

    public void setSendMode(SendMode sendMode) {
        this.sendMode = sendMode;
    }

    public SendMode getSendMode() {
        return sendMode;
    }

    public TagConfig getTagConfig() {
        return tagConfig;
    }

    public void setTagConfig(TagConfig tagConfig) {
        this.tagConfig = tagConfig;
    }

    @PostConstruct
    public void begin() throws Exception {
        TracerClientStarter tracerClientStarter = new TracerClientStarter.Builder()
                .setAppName("demo")
                .setTagConfig(tagConfig)
                .setSendMode(sendMode)
                .build();
        logger.info("初始化tagConfig: {}",tagConfig);
        tracerClientStarter.startPipeline();
    }

    @Bean
    public FilterRegistrationBean urlFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        HttpFilter userFilter = new HttpFilter();
        registration.setFilter(userFilter);
        registration.addUrlPatterns("/*");
        registration.setName("UserTraceFilter");
        registration.setOrder(1);
        return registration;
    }
}
