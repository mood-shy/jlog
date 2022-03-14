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
import static com.jd.platform.jlog.core.Constant.*;
import static com.jd.platform.jlog.nacos.NacosConstant.*;



/**
 * @author tangbohu
 */
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
                configService.addListener(DEFAULT_DATA_ID, JLOG_GROUP, new NacosListener());

            } catch (NacosException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public String getString(String key) {
        return null;
    }

    @Override
    public Long getLong(String key) {
        return null;
    }

    @Override
    public List<String> getList(String key) {
        return null;
    }

    @Override
    public <T> T getObject(String key, Class<T> clz) {
        return null;
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



    private static Properties getConfigProperties() {
        Properties properties = new Properties();
        String address = FILE_CONFIG.getString(SERVER_ADDR_KEY);
        if (address != null) {
            properties.setProperty(SERVER_ADDR_KEY, address);
        }

        String namespace = FILE_CONFIG.getString(NAMESPACE_KEY);
        if (namespace != null) {
            properties.setProperty(NAMESPACE_KEY, namespace);
        }else{
            if (System.getProperty(NAMESPACE_KEY) != null) {
                properties.setProperty(NAMESPACE_KEY, System.getProperty(NAMESPACE_KEY));
            }
        }
        return properties;
    }



    @Override
    public String getType() {
        return "nacos";
    }


}
