package com.jd.platform.jlog.nacos;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;

import com.alibaba.nacos.common.utils.StringUtils;
import com.jd.platform.jlog.core.ConfigChangeListener;
import com.jd.platform.jlog.core.Configurator;
import com.jd.platform.jlog.core.ConfiguratorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jd.platform.jlog.nacos.NacosConstant.*;


/**
 * The type Nacos configuration.
 *
 * @author slievrly
 */
public class NacosConfigurator implements Configurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NacosConfigurator.class);


    private static volatile NacosConfigurator instance;


    private static final Configurator FILE_CONFIG = ConfiguratorFactory.base;

    private static volatile ConfigService configService;

    static final ConcurrentMap<String, ConfigChangeListener> CONFIG_LISTENER_MAP = new ConcurrentHashMap<>(8);

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
                LOGGER.info("实例化NacosConfigurator完成 result = {}",configService.getServerStatus());
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
                value = configService.getConfig(key, JLOG_GROUP, timeoutMills);
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
                result = configService.publishConfig(DEFAULT_DATA_ID, JLOG_GROUP, getConfigStr());
            } else {
                result = configService.publishConfig(dataId, JLOG_GROUP, content);
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
                result = configService.publishConfig(DEFAULT_DATA_ID, JLOG_GROUP, getConfigStr());
            } else {
                result = configService.removeConfig(dataId, JLOG_GROUP);
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
    public void addConfigListener(String dataId) {
        LOGGER.info("nacos添加监听器开始 dataId:{}",dataId);
        NacosListener nacosListener = new NacosListener(dataId);
        CONFIG_LISTENER_MAP.put(dataId, nacosListener);
        LOGGER.info("## nacos添加监听器过程 {}",nacosListener.toString());

        try {
            configService.addListener(dataId, JLOG_GROUP, nacosListener);
        } catch (NacosException e) {
            LOGGER.error("nacos添加监听器失败",e);
        }
    }



    @Override
    public void removeConfigListener(String dataId) {
        if (StringUtils.isBlank(dataId)) {
            return;
        }
        LOGGER.info("nacos移除监听器开始");
        ConfigChangeListener changeListener = getConfigListeners(dataId);
        NacosListener listener = (NacosListener) changeListener;
        LOGGER.info("## nacos移除监听器过程 {}",listener.toString());

        CONFIG_LISTENER_MAP.remove(dataId);
        configService.removeListener(dataId, JLOG_GROUP, listener);
    }

    @Override
    public ConfigChangeListener getConfigListeners(String dataId) {
        return CONFIG_LISTENER_MAP.get(dataId);
    }


    private static Properties getConfigProperties() {
        Properties properties = new Properties();
        String address = FILE_CONFIG.getConfig(PRO_SERVER_ADDR_KEY);
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
            String config = configService.getConfig(DEFAULT_DATA_ID, JLOG_GROUP, 2000L);
            LOGGER.info("从NaCos获取配置进行初始化 config = {}", config);

            if (StringUtils.isNotBlank(config)) {
                try (Reader reader = new InputStreamReader(new ByteArrayInputStream(config.getBytes()), StandardCharsets.UTF_8)) {
                    props.load(reader);
                }
                LOGGER.info("初始化本地缓存 props:{} ", JSON.toJSONString(props));
                NacosListener nacosListener = new NacosListener(DEFAULT_DATA_ID);
                configService.addListener(DEFAULT_DATA_ID, JLOG_GROUP, nacosListener);
            }
        } catch (NacosException | IOException e) {
            LOGGER.error("init config properties error", e);
        }
    }

    @Override
    public String getType() {
        return "nacos";
    }


    @Override
    public Map<String, String> getConfigByPrefix(String prefix) {
        return null;
    }


}
