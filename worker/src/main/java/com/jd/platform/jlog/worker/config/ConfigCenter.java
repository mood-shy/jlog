package com.jd.platform.jlog.worker.config;


import com.jd.platform.jlog.common.tag.TagConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

    private TagConfig tagConfig ;

    public TagConfig getTagConfig() {
        return tagConfig;
    }

    public void setTagConfig(TagConfig tagConfig) {
        this.tagConfig = tagConfig;
    }

}
