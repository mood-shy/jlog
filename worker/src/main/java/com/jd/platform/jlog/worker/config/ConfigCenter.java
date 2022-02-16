package com.jd.platform.jlog.worker.config;

import com.jd.platform.jlog.common.config.ConfigCenterEnum;
import com.jd.platform.jlog.common.config.ConfigCenterFactory;
import com.jd.platform.jlog.common.config.IConfigCenter;
import com.jd.platform.jlog.common.model.CenterConfig;
import com.jd.platform.jlog.common.model.TagConfig;
import com.jd.platform.jlog.common.utils.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


/**
 * EtcdConfig
 * @author wuweifeng wrote on 2019-12-06
 * @version 1.0
 */
@Component
@ConfigurationProperties(prefix = "jlog")
public class ConfigCenter {

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

    @Bean
    public IConfigCenter client() throws Exception {

        ConfigCenterFactory.buildConfigCenter(centerConfig);
        //连接多个时，逗号分隔
        //return JdEtcdBuilder.build(etcdServer);
        return ConfigCenterFactory.getClient(ConfigUtil.getCenter(centerConfig));
    }

}
