package com.jd.platform.jlog.clientdemo.config;

import com.jd.platform.jlog.client.TracerClientStarter;
import com.jd.platform.jlog.clientdemo.interceptor.UserInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;

/**
 * 配置信息
 *
 * @author shenkaiwen5
 * @version 1.0
 * @date 2021-12-27
 */
@Configuration
public class DemoConfig implements WebMvcConfigurer {

    @Value("${config.server}")
    private String etcdServer;

    @PostConstruct
    public void begin() {
        TracerClientStarter tracerClientStarter = new TracerClientStarter.Builder()
                .setAppName("demo")
                .setEtcdServer(etcdServer).build();
        tracerClientStarter.startPipeline();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterceptor()).addPathPatterns("/*");
    }
}
