package com.jd.platform.jlog.clientdemo.config;

import com.jd.platform.jlog.client.TracerClientStarter;
import com.jd.platform.jlog.client.filter.UserFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 配置信息
 *
 * @author shenkaiwen5
 * @version 1.0
 * @date 2021-12-27
 */
@Configuration
public class DemoConfig {

    @Value("${config.server}")
    private String etcdServer;

    @PostConstruct
    public void begin() {
        TracerClientStarter tracerClientStarter = new TracerClientStarter.Builder()
                .setAppName("demo")
                .setEtcdServer(etcdServer).build();
        tracerClientStarter.startPipeline();
    }

    @Bean
    public FilterRegistrationBean urlFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        UserFilter userFilter = new UserFilter();

        registration.setFilter(userFilter);
        registration.addUrlPatterns("/*");
        registration.setName("UserTraceFilter");
        registration.setOrder(1);
        return registration;
    }
}
