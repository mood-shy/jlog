/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
            /**
             * 配置文件的配置器
             */
            base = new FileConfigurator();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * 配置中心的配置器，如果没有就用文件配置器
         */
        ServiceLoader<ConfiguratorProvider> builders = ServiceLoader.load(ConfiguratorProvider.class);
        for (ConfiguratorProvider provider : builders) {
            LOGGER.info("第一个实现类类型为:{}", provider.build().getType());
            return provider.build();
        }
        return base;
    }

    /**
     * 重载
     * @throws Exception
     */
    protected static void reload() throws Exception {
        instance = null;
        getInstance();
    }
}
