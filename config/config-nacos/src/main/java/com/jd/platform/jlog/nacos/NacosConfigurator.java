package com.jd.platform.jlog.nacos;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;

import com.alibaba.nacos.common.utils.StringUtils;
import com.jd.platform.jlog.common.utils.StringUtil;
import com.jd.platform.jlog.core.ConfigChangeListener;
import com.jd.platform.jlog.core.Configurator;
import com.jd.platform.jlog.core.ConfiguratorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jd.platform.jlog.common.utils.ConfigUtil.formatConfigStr;
import static com.jd.platform.jlog.core.Constant.DEFAULT_TIMEOUT;
import static com.jd.platform.jlog.nacos.NacosConstant.*;



public class NacosConfigurator implements Configurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NacosConfigurator.class);


    private static volatile NacosConfigurator instance;


    private static final Configurator FILE_CONFIG = ConfiguratorFactory.base;

    private static volatile ConfigService configService;

    static final ConcurrentMap<String, String> KEY_DATAID_MAP = new ConcurrentHashMap<>(8);

    static volatile Properties PROPERTIES = new Properties();

    static NacosListener NACOSLISTENER = new NacosListener();


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
                String config = configService.getConfig(DEFAULT_DATA_ID, JLOG_GROUP, DEFAULT_TIMEOUT);
                LOGGER.info("从NaCos获取配置进行初始化 config = {}", config);
                if (StringUtils.isNotBlank(config)) {
                    PROPERTIES.load(new StringReader(config));
                    LOGGER.info("初始化本地缓存 props:{} ", JSON.toJSONString(PROPERTIES));
                    configService.addListener(DEFAULT_DATA_ID, JLOG_GROUP, NACOSLISTENER);
                    Enumeration<?> e = PROPERTIES.propertyNames();
                    while (e.hasMoreElements()) {
                        KEY_DATAID_MAP.put((String) e.nextElement(), DEFAULT_DATA_ID);
                    }
                }
            } catch (NacosException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public String getConfig(String key) {
        return getConfig(key, DEFAULT_TIMEOUT);
    }


    @Override
    public String getConfig(String key, long timeoutMills) {
        String value = System.getProperty(key);
        if (value != null) {
            return value;
        }

        value = PROPERTIES.getProperty(key);

        if (null == value) {
            try {
                String dataId = KEY_DATAID_MAP.get(key);
                if(StringUtil.isEmpty(dataId)){
                    return null;
                }
                String config = configService.getConfig(dataId, JLOG_GROUP, timeoutMills);
                if(StringUtil.isEmpty(config)){
                    return null;
                }
                PROPERTIES.load(new StringReader(config));
            } catch (NacosException | IOException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
        return PROPERTIES.getProperty(key);
    }


    @Override
    public boolean putConfig(String key, String content) {
        return  putConfig(key, content, DEFAULT_TIMEOUT);
    }


    @Override
    public boolean putConfig(String key, String content, long timeoutMills) {
        boolean result = false;
        String dataId = KEY_DATAID_MAP.get(key);
        if(StringUtil.isEmpty(dataId)){
            return false;
        }
        try {
            if (!PROPERTIES.isEmpty()) {
                PROPERTIES.setProperty(key, content);
                result = configService.publishConfig(dataId, JLOG_GROUP, formatConfigStr(PROPERTIES));
            } else {
                result = configService.publishConfig(dataId, JLOG_GROUP, content);
            }
        } catch (NacosException ex) {
            LOGGER.error(ex.getErrMsg());
        }
        return result;
    }


    @Override
    public boolean removeConfig(String key) {
        return removeConfig(key, DEFAULT_TIMEOUT);
    }



    @Override
    public boolean removeConfig(String key, long timeoutMills) {

        String dataId = KEY_DATAID_MAP.get(key);
        if(StringUtil.isEmpty(dataId)){
            return false;
        }
        boolean result = false;
        try {
            if (!PROPERTIES.isEmpty()) {
                PROPERTIES.remove(key);
                result = configService.publishConfig(dataId, JLOG_GROUP, formatConfigStr(PROPERTIES));
            } else {
                result = configService.removeConfig(dataId, JLOG_GROUP);
            }
        } catch (NacosException exx) {
            LOGGER.error(exx.getErrMsg());
        }
        return result;
    }



    @Override
    public void addConfigListener(String dataId) {
        if(!DEFAULT_DATA_ID.equals(dataId)){
            throw new RuntimeException("no support");
        }
        LOGGER.info("nacos添加监听器开始 dataId:{}",dataId);
        NacosListener nacosListener = new NacosListener();
        LOGGER.info("## nacos添加监听器过程 {}",nacosListener.toString());
        try {
            configService.addListener(dataId, JLOG_GROUP, nacosListener);
        } catch (NacosException e) {
            LOGGER.error("nacos添加监听器失败",e);
        }
    }



    @Override
    public void removeConfigListener(String dataId) {
        if(!DEFAULT_DATA_ID.equals(dataId)){
            throw new RuntimeException("no support");
        }
        LOGGER.info("nacos移除监听器开始");
        configService.removeListener(dataId, JLOG_GROUP, NACOSLISTENER);
    }


    private static Properties getConfigProperties() {
        Properties properties = new Properties();
        String address = FILE_CONFIG.getConfig(PRO_SERVER_ADDR_KEY);
        if (address != null) {
            properties.setProperty(PRO_SERVER_ADDR_KEY, address);
        }
        return properties;
    }



    @Override
    public String getType() {
        return "nacos";
    }


    @Override
    public List<String> getConfigByPrefix(String prefix) {
        return null;
    }


}
