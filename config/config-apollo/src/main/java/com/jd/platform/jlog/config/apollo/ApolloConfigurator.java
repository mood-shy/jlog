package com.jd.platform.jlog.config.apollo;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;

import com.ctrip.framework.apollo.core.utils.StringUtils;
import com.ctrip.framework.apollo.enums.PropertyChangeType;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.jd.platform.jlog.core.*;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static com.ctrip.framework.apollo.core.ApolloClientSystemConsts.APP_ID;
import static com.jd.platform.jlog.config.apollo.ApolloConstant.*;


/**
 * @author tangbohu
 * @version 1.0.0
 * @Description todo env and cluster
 * @ClassName ApolloConfigurator.java
 * @createTime 2022年02月21日 21:21:00
 */
public class ApolloConfigurator implements Configurator {


    private static final Logger LOGGER = LoggerFactory.getLogger(ApolloConfigurator.class);


    private static final Configurator FILE_CONFIG = ConfiguratorFactory.base;
    /**
     * 里面有resourceProperties 和 configProperties
     */
    private static volatile Config config;
    private static final ConcurrentMap<String, ConfigChangeListener> CONFIG_LISTENER_MAP = new ConcurrentHashMap<>();
    private static volatile ApolloConfigurator instance;

    private ApolloConfigurator() {
        loadApolloServerConfig();
        if (config == null) {
            synchronized (ApolloConfigurator.class) {
                if (config == null) {
                    // apollo的监听是按照namespace维度
                    config = ConfigService.getConfig(DEFAULT_NAMESPACE);
                    config.addChangeListener(changeEvent -> {
                        LOGGER.info("Apollo收到事件变更, keys={}", changeEvent.changedKeys());
                        for (String key : changeEvent.changedKeys()) {
                            if (!CONFIG_LISTENER_MAP.containsKey(key)) {
                                LOGGER.info("{}不是关注的key,直接返回", key);
                                continue;
                            }
                            ConfigChange change = changeEvent.getChange(key);
                            ConfigChangeEvent event = new ConfigChangeEvent(key, change.getNamespace(),
                                    change.getOldValue(), change.getNewValue(), getChangeType(change.getChangeType()));
                            CONFIG_LISTENER_MAP.get(key).onProcessEvent(event);
                        }
                    });
                }
            }
        }
        System.out.println("Apollo配置器构建完成");
    }


    public static ApolloConfigurator getInstance() {
        if (instance == null) {
            synchronized (ApolloConfigurator.class) {
                if (instance == null) {
                    instance = new ApolloConfigurator();
                }
            }
        }
        return instance;
    }

    @Override
    public String getConfig(String key) {
        return config.getProperty(key,"");
    }

    @Override
    public String getConfig(String key, long timeoutMills) {
        return getConfig(key,1L);
    }


    @Override
    public boolean putConfig(String key, String content) {
        return false;
    }


    @Override
    public boolean putConfig(String dataId, String content, long timeoutMills) {
        return false;
    }



    @Override
    public boolean removeConfig(String dataId, long timeoutMills) {
        return false;
    }


    @Override
    public boolean removeConfig(String key) {
        return false;
    }

    @Override
    public void addConfigListener(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        CONFIG_LISTENER_MAP.put(key, new ApolloListener());
    }


    @Override
    public void removeConfigListener(String key) {
        System.out.println("Apollo进入移除"+key);
        CONFIG_LISTENER_MAP.remove(key);
    }


    @Override
    public ConfigChangeListener getConfigListeners(String dataId) {
        return CONFIG_LISTENER_MAP.get(dataId);
    }


    @Override
    public String getType() {
        return "apollo";
    }


    @Override
    public Map<String, String> getConfigByPrefix(String prefix) {
        return null;
    }


    private ConfigChangeType getChangeType(PropertyChangeType changeType) {
        switch (changeType) {
            case ADDED:
                return ConfigChangeType.ADD;
            case DELETED:
                return ConfigChangeType.DELETE;
            default:
                return ConfigChangeType.MODIFY;
        }
    }


    private void loadApolloServerConfig() {

        Properties properties = System.getProperties();
        if (!properties.containsKey(PROP_APP_ID)) {
            System.setProperty(PROP_APP_ID, FILE_CONFIG.getConfig(APP_ID));
        }
        if (!properties.containsKey(PROP_APOLLO_META)) {
            System.setProperty(PROP_APOLLO_META, FILE_CONFIG.getConfig(APOLLO_META));
        }
        if (!properties.containsKey(PROP_APOLLO_SECRET)) {
            String secretKey = FILE_CONFIG.getConfig(APOLLO_SECRET);
            if (!StringUtils.isBlank(secretKey)) {
                System.setProperty(PROP_APOLLO_SECRET, secretKey);
            }
        }
        if (!properties.containsKey(APOLLO_CLUSTER)) {
            if (!StringUtils.isBlank(FILE_CONFIG.getConfig(APOLLO_CLUSTER))) {
                System.setProperty(PROP_APOLLO_CLUSTER, FILE_CONFIG.getConfig(APOLLO_CLUSTER));
            }
        }
        if (!properties.containsKey(APOLLO_CONFIG_SERVICE)) {
            System.setProperty(PROP_APOLLO_CONFIG_SERVICE, FILE_CONFIG.getConfig(APOLLO_CONFIG_SERVICE));
        }
    }
}
