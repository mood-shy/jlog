package com.jd.platform.jlog.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ServiceLoader;


/**
 * @author tangbohu
 * @version 1.0.0
 * @desc 参考log4J
 * @ClassName ConfiguratorFactory.java
 * @createTime 2022年02月15日 21:54:00
 */
public class ConfiguratorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfiguratorFactory.class);


    private static volatile Configurator instance = null;


    public static volatile Configurator base = null;



    public static Configurator getInstance(){
        if (instance == null) {
            synchronized (Configurator.class) {
                if (instance == null) {
                    instance = buildConfiguration();
                    LOGGER.info("构建总配置器单例完成 instance 获取reqTags结果:{}", instance.getConfig("reqTags"));
                    LOGGER.info("构建总配置器单例完成 Base 获取serverAddr结果:{}", base.getConfig("serverAddr"));
                }
            }
        }
        return instance;
    }


    /**
     * SPI实现装载不同的配置器
     * @return 配置器
     */
    private static Configurator buildConfiguration() {

        try {
            /*
              配置文件的配置器
             */
            base = new FileConfigurator();
        } catch (IOException e) {
            LOGGER.info("文件配置器构建失败", e);
            throw new RuntimeException("build file buildConfiguration fail", e);
        }

        /*
          配置中心的配置器，如果没有就用文件配置器
         */
        ServiceLoader<ConfiguratorProvider> builders = ServiceLoader.load(ConfiguratorProvider.class);
        //noinspection LoopStatementThatDoesntLoop
        for (ConfiguratorProvider provider : builders) {
            LOGGER.info("配置中心的配置器获取成功, 类型为:{}", provider.build().getType());
            return provider.build();
        }
        return base;
    }

}
