package com.jd.platform.jlog.nacos;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractSharedListener;
import com.alibaba.nacos.api.exception.NacosException;

import com.alibaba.nacos.common.utils.StringUtils;
import com.jd.platform.jlog.core.ConfigChangeEvent;
import com.jd.platform.jlog.core.ConfigChangeListener;
import com.jd.platform.jlog.core.Configurator;
import com.jd.platform.jlog.core.ConfiguratorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.alibaba.nacos.api.common.Constants.DEFAULT_GROUP;
import static com.jd.platform.jlog.nacos.NacosConstant.*;


/**
 * The type Nacos configuration.
 *
 * @author slievrly
 */
public class NacosConfigurator implements Configurator {

    private static volatile NacosConfigurator instance;

    private static final Logger LOGGER = LoggerFactory.getLogger(NacosConfigurator.class);

    private static final Configurator FILE_CONFIG = ConfiguratorFactory.base;
    private static volatile ConfigService configService;

    static final ConcurrentMap<String, ConcurrentMap<ConfigChangeListener, NacosListener>> CONFIG_LISTENERS_MAP = new ConcurrentHashMap<>(8);

    static volatile Properties props = new Properties();


    public static NacosConfigurator getInstance() {
        if (instance == null) {
            synchronized (NacosConfigurator.class) {
                if (instance == null) {
                    instance = new NacosConfigurator();
                }
            }
        }
        return instance;
    }


    private NacosConfigurator() {
        if (configService == null) {
            try {
                configService = NacosFactory.createConfigService(getConfigProperties());
                initConfig();
            } catch (NacosException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public String getConfig(String key) {
        return null;
    }

    @Override
    public String getConfig(String key, long timeoutMills) {
        String value = System.getProperty(key);
        if (value != null) {
            return value;
        }

        value = props.getProperty(key);

        if (null == value) {
            try {
                value = configService.getConfig(key, DEFAULT_GROUP, timeoutMills);
            } catch (NacosException exx) {
                LOGGER.error(exx.getErrMsg());
            }
        }

        return value;
    }

    @Override
    public boolean putConfig(String key, String content) {
        return false;
    }

    @Override
    public boolean putConfig(String dataId, String content, long timeoutMills) {
        boolean result = false;
        try {
            if (!props.isEmpty()) {
                props.setProperty(dataId, content);
                result = configService.publishConfig(DEFAULT_DATA_ID, DEFAULT_GROUP, getConfigStr());
            } else {
                result = configService.publishConfig(dataId, DEFAULT_GROUP, content);
            }
        } catch (NacosException exx) {
            LOGGER.error(exx.getErrMsg());
        }
        return result;
    }


    @Override
    public boolean removeConfig(String dataId, long timeoutMills) {
        boolean result = false;
        try {
            if (!props.isEmpty()) {
                props.remove(dataId);
                result = configService.publishConfig(DEFAULT_DATA_ID, DEFAULT_GROUP, getConfigStr());
            } else {
                result = configService.removeConfig(dataId, DEFAULT_GROUP);
            }
        } catch (NacosException exx) {
            LOGGER.error(exx.getErrMsg());
        }
        return result;
    }

    @Override
    public boolean removeConfig(String key) {
        return false;
    }



    @Override
    public void addConfigListener(String dataId, ConfigChangeListener listener) {
        if (StringUtils.isBlank(dataId) || listener == null) {
            return;
        }
        try {
            NacosListener nacosListener = new NacosListener(dataId, listener);
            CONFIG_LISTENERS_MAP.computeIfAbsent(dataId, key -> new ConcurrentHashMap<>())
                    .put(listener, nacosListener);
            configService.addListener(dataId, DEFAULT_GROUP, nacosListener);
        } catch (Exception exx) {
            LOGGER.error("add nacos listener error:{}", exx.getMessage(), exx);
        }
    }

    @Override
    public void removeConfigListener(String dataId, ConfigChangeListener listener) {
        if (StringUtils.isBlank(dataId) || listener == null) {
            return;
        }
        Set<ConfigChangeListener> configChangeListeners = getConfigListeners(dataId);
        if (configChangeListeners != null && configChangeListeners.size() > 0) {
            for (ConfigChangeListener entry : configChangeListeners) {
                if (listener.equals(entry)) {
                    NacosListener nacosListener = null;
                    Map<ConfigChangeListener, NacosListener> configListeners = CONFIG_LISTENERS_MAP.get(dataId);
                    if (configListeners != null) {
                        nacosListener = configListeners.get(listener);
                        configListeners.remove(entry);
                    }
                    if (nacosListener != null) {
                        configService.removeListener(dataId, DEFAULT_GROUP, nacosListener);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public Set<ConfigChangeListener> getConfigListeners(String dataId) {
        Map<ConfigChangeListener, NacosListener> configListeners = CONFIG_LISTENERS_MAP.get(dataId);
        if (configListeners != null && configListeners.size() > 0) {
            return configListeners.keySet();
        } else {
            return null;
        }
    }


    private static Properties getConfigProperties() {
        Properties properties = new Properties();
        properties.setProperty(PRO_NAMESPACE_KEY, DEFAULT_NAMESPACE);
        String address = FILE_CONFIG.getConfig("serverAddr",2000L);
        if (address != null) {
            properties.setProperty(PRO_SERVER_ADDR_KEY, address);
        }
        return properties;
    }



    private static String getConfigStr() {
        StringBuilder sb = new StringBuilder();

        Enumeration<?> enumeration = props.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            String property = props.getProperty(key);
            sb.append(key).append("=").append(property).append("\n");
        }

        return sb.toString();
    }

    private static void initConfig() {
        try {
            String config = configService.getConfig(DEFAULT_DATA_ID, DEFAULT_GROUP, 2000L);
            if (StringUtils.isNotBlank(config)) {
                try (Reader reader = new InputStreamReader(new ByteArrayInputStream(config.getBytes()), StandardCharsets.UTF_8)) {
                    props.load(reader);
                }
                NacosListener nacosListener = new NacosListener(DEFAULT_DATA_ID, null);
                configService.addListener(DEFAULT_DATA_ID, DEFAULT_GROUP, nacosListener);
            }
        } catch (NacosException | IOException e) {
            LOGGER.error("init config properties error", e);
        }
    }

    @Override
    public String getType() {
        return CONFIG_TYPE;
    }

    @Override
    public Map<String, String> getConfigByPrefix(String prefix) {
        return null;
    }


}
