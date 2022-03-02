package com.jd.platform.jlog.etcd;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.ByteString;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.api.RangeResponse;
import com.ibm.etcd.client.EtcdClient;
import com.jd.platform.jlog.common.utils.CollectionUtil;
import com.jd.platform.jlog.common.utils.StringUtil;
import com.jd.platform.jlog.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jd.platform.jlog.common.utils.ConfigUtil.formatConfigByte;
import static com.jd.platform.jlog.common.utils.ConfigUtil.formatConfigStr;
import static com.jd.platform.jlog.core.Constant.DEFAULT_TIMEOUT;
import static com.jd.platform.jlog.core.Constant.SERVER_ADDR_KEY;

/**
 * @author tangbohu
 * @version 1.0.0
 * @desc 参考log4J
 * @ClassName EtcdConfigurator.java
 * @createTime 2022年02月21日 21:46:00
 */
public class EtcdConfigurator implements Configurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(EtcdConfigurator.class);

    private static volatile EtcdConfigurator instance;

    static volatile EtcdClient client;

    private static final Configurator FILE_CONFIG = ConfiguratorFactory.base;


    private static final String PROPERTIES_PATH = "/jLog/properties";

    private static Properties PROPERTIES = new Properties();

    private volatile static EtcdListener LISTENER = null;


    private EtcdConfigurator() {
        LOGGER.info("开始构建etcd客户端, serverAddr:{}",FILE_CONFIG.getConfig(SERVER_ADDR_KEY));
        client = EtcdClient.forEndpoints(FILE_CONFIG.getConfig(SERVER_ADDR_KEY,2000L)).withPlainText().build();
        RangeResponse rangeResponse = client.getKvClient().get(ByteString.copyFromUtf8(PROPERTIES_PATH)).sync();
        List<KeyValue> keyValues = rangeResponse.getKvsList();
        if (CollectionUtil.isEmpty(keyValues)) {
            return;
        }
        String val = keyValues.get(0).getValue().toStringUtf8();
        if(StringUtil.isNotBlank(val)){
            PROPERTIES.putAll((Map)JSON.parse(val));
        }
    }


    public static EtcdConfigurator getInstance() {
        if (instance == null) {
            synchronized (EtcdConfigurator.class) {
                if (instance == null) {
                    instance = new EtcdConfigurator();
                }
            }
        }
        return instance;
    }


    @Override
    public String getConfig(String key) {
        Object val = PROPERTIES.get(key);
        if(val != null){
            return String.valueOf(val);
        }
        return null;
    }

    @Override
    public String getConfig(String key, long timeoutMills) {
        return getConfig(key);
    }

    @Override
    public boolean putConfig(String key, String content) {
        return putConfig(key, content, DEFAULT_TIMEOUT);
    }

    @Override
    public boolean putConfig(String key, String content, long timeoutMills) {
        if(StringUtil.isEmpty(key) || StringUtil.isEmpty(content)){
            return false;
        }
        PROPERTIES.setProperty(key, content);
        client.getKvClient().put(ByteString.copyFromUtf8(PROPERTIES_PATH), ByteString.copyFromUtf8(formatConfigStr(PROPERTIES))).sync();
        return true;
    }

    @Override
    public boolean removeConfig(String key) {
        return removeConfig(key, DEFAULT_TIMEOUT);
    }


    @Override
    public boolean removeConfig(String key, long timeoutMills) {
        PROPERTIES.remove(key);
        client.getKvClient().put(ByteString.copyFromUtf8(PROPERTIES_PATH), ByteString.copyFromUtf8(formatConfigStr(PROPERTIES))).sync();
        return true;
    }

    @Override
    public void addConfigListener(String node) {
        System.out.println("添加etcd监听器"+node);
        LISTENER = new EtcdListener(node);
        LISTENER.onProcessEvent(new ConfigChangeEvent());
    }

    @Override
    public void removeConfigListener(String node) {
        System.out.println("移除etcd监听器"+node);
        LISTENER.onShutDown();
        LISTENER = null;
    }

    @Override
    public String getType() {
        return "etcd";
    }


    @Override
    public List getConfigByPrefix(String prefix) {
        RangeResponse rangeResponse = client.getKvClient().get(ByteString.copyFromUtf8(prefix)).asPrefix().sync();
        List<KeyValue> keyValues = rangeResponse.getKvsList();
        List list = new ArrayList<>();
        for (KeyValue kv : keyValues) {
            list.add(kv.getValue().toStringUtf8());
        }
        return list;
    }

}
