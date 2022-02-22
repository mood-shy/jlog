package com.jd.platform.jlog.etcd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.google.protobuf.ByteString;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.api.RangeResponse;
import com.ibm.etcd.client.EtcdClient;
import com.jd.platform.jlog.common.utils.CollectionUtil;
import com.jd.platform.jlog.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author didi
 */
public class EtcdConfigurator implements Configurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(EtcdConfigurator.class);

    private static volatile EtcdConfigurator instance;

    static volatile EtcdClient client;

    private static final Configurator FILE_CONFIG = ConfiguratorFactory.base;
    private static final String SERVER_ADDR_KEY = "serverAddr";
    private ConcurrentMap<String, ConfigChangeListener> configListenerMap = new ConcurrentHashMap<>(8);


    private EtcdConfigurator() {
        LOGGER.info("开始构建etcd客户端, serverAddr:{}",FILE_CONFIG.getConfig(SERVER_ADDR_KEY));
        client = EtcdClient.forEndpoints(FILE_CONFIG.getConfig(SERVER_ADDR_KEY,2000L)).withPlainText().build();
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
        return false;
    }


    @Override
    public boolean removeConfig(String key, long timeoutMills) {
        client.getKvClient().delete(ByteString.copyFromUtf8(key)).timeout(timeoutMills).sync();
        return false;
    }

    @Override
    public void addConfigListener(String key) {
        EtcdListener etcdListener = new EtcdListener(key);
        configListenerMap.put(key, etcdListener);
        etcdListener.onProcessEvent(new ConfigChangeEvent());
    }

    @Override
    public void removeConfigListener(String key) {
        ConfigChangeListener configListener = getConfigListeners(key);
        configListenerMap.remove(key);
        configListener.onShutDown();
    }


    @Override
    public ConfigChangeListener getConfigListeners(String dataId) {
        return configListenerMap.get(dataId);
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
