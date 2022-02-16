package com.jd.platform.jlog.clientdemo.config;

import com.jd.platform.jlog.client.TracerClientStarter;
import com.jd.platform.jlog.client.filter.HttpFilter;
import com.jd.platform.jlog.common.model.CenterConfig;
import com.jd.platform.jlog.common.model.TagConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

/**
 * 配置信息
 *
 * @author shenkaiwen5
 * @version 1.0
 * @date 2021-12-27
 */
@Component
@ConfigurationProperties(prefix = "jlog")
public class DemoConfig {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private CenterConfig centerConfig;
    private TagConfig tagConfig ;

    public CenterConfig getCenterConfig() {
        return centerConfig;
    }

    public void setCenterConfig(CenterConfig centerConfig) {
        this.centerConfig = centerConfig;
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
                .setCenterConfig(centerConfig)
                .setTagConfig(tagConfig)
                .build();
        logger.info("init centerConfig",centerConfig);
        logger.info("init tagConfig",tagConfig);
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
