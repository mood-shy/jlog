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

    private ConcurrentMap<String, ConfigChangeListener> configListenerMap = new ConcurrentHashMap<>(8);

    private static final String SERVER_ADDR_KEY = "serverAddr";

    private static final String ROOT = "/jLog";

    private static final String PROPERTIES_PATH = "/properties";

    private static Properties PROPERTIES = new Properties();



    private EtcdConfigurator() {
        LOGGER.info("开始构建etcd客户端, serverAddr:{}",FILE_CONFIG.getConfig(SERVER_ADDR_KEY));
        client = EtcdClient.forEndpoints(FILE_CONFIG.getConfig(SERVER_ADDR_KEY,2000L)).withPlainText().build();
        String val = getConfig(ROOT + PROPERTIES_PATH);
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
        RangeResponse rangeResponse = client.getKvClient().get(ByteString.copyFromUtf8(key)).sync();
        List<KeyValue> keyValues = rangeResponse.getKvsList();
        if (CollectionUtil.isEmpty(keyValues)) {
            return null;
        }
        return keyValues.get(0).getValue().toStringUtf8();
    }

    @Override
    public String getConfig(String key, long timeoutMills) {
        RangeResponse rangeResponse = client.getKvClient().get(ByteString.copyFromUtf8(key)).timeout(timeoutMills).sync();
        List<KeyValue> keyValues = rangeResponse.getKvsList();
        if (CollectionUtil.isEmpty(keyValues)) {
            return null;
        }
        return keyValues.get(0).getValue().toStringUtf8();
    }

    @Override
    public boolean putConfig(String key, String content) {
        client.getKvClient().put(ByteString.copyFromUtf8(key), ByteString.copyFromUtf8(content)).sync();
        return true;
    }

    @Override
    public boolean putConfig(String key, String content, long timeoutMills) {
        client.getKvClient().put(ByteString.copyFromUtf8(key), ByteString.copyFromUtf8(content)).timeout(timeoutMills).sync();
        return true;
    }

    @Override
    public boolean removeConfig(String key) {
        client.getKvClient().delete(ByteString.copyFromUtf8(key)).sync();
        return true;
    }


    @Override
    public boolean removeConfig(String key, long timeoutMills) {
        client.getKvClient().delete(ByteString.copyFromUtf8(key)).timeout(timeoutMills).sync();
        return true;
    }

    @Override
    public void addConfigListener(String key) {
        System.out.println("添加etcd监听器"+key);
        EtcdListener etcdListener = new EtcdListener(key);
        configListenerMap.put(key, etcdListener);
        etcdListener.onProcessEvent(new ConfigChangeEvent());
    }

    @Override
    public void removeConfigListener(String key) {
        System.out.println("移除etcd监听器"+key);
        ConfigChangeListener configListener = getConfigListeners(key);
        configListenerMap.remove(key);
        configListener.onShutDown();
    }


    @Override
    public ConfigChangeListener getConfigListeners(String key) {
        return configListenerMap.get(key);
    }


    @Override
    public String getType() {
        return "etcd";
    }


    @Override
    public Map<String,String> getConfigByPrefix(String prefix) {
        RangeResponse rangeResponse = client.getKvClient().get(ByteString.copyFromUtf8(prefix)).asPrefix().sync();
        List<KeyValue> list = rangeResponse.getKvsList();
        Map<String, String> map = new HashMap<>(list.size());
        for (KeyValue kv : list) {
            map.put(kv.getKey().toStringUtf8(), kv.getValue().toStringUtf8());
        }
        return map;
    }

}
