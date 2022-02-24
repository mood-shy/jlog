package com.jd.platform.jlog.zk;

import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;

import com.alibaba.fastjson.JSON;
import com.jd.platform.jlog.common.utils.StringUtil;
import com.jd.platform.jlog.core.*;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jd.platform.jlog.zk.ZkConstant.DEFAULT_CONFIG_PATH;
import static com.jd.platform.jlog.zk.ZkConstant.NAMESPACE;
import static com.jd.platform.jlog.zk.ZkConstant.SERVER_ADDR_KEY;


/**
 * @author didi
 */
public class ZkConfigurator implements Configurator {

    private final static Logger LOGGER = LoggerFactory.getLogger(ZkConfigurator.class);

    private static final Configurator FILE_CONFIG = ConfiguratorFactory.base;

    static volatile CuratorFramework zkClient;

    static final ConcurrentMap<String, ConfigChangeListener> CONFIG_LISTENERS_MAP = new ConcurrentHashMap<>(8);
    static volatile Properties pros = new Properties();


    public ZkConfigurator() throws Exception {
        if (zkClient == null) {
            synchronized (ZkConfigurator.class) {
                zkClient = CuratorFrameworkFactory.builder().connectString(FILE_CONFIG.getConfig(SERVER_ADDR_KEY))
                        // 连接超时时间
                        .sessionTimeoutMs(2000)
                        // 会话超时时间
                        .connectionTimeoutMs(6000)
                        .namespace(NAMESPACE)
                        // 刚开始重试间隔为1秒，之后重试间隔逐渐增加，最多重试不超过三次
                        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                        .build();
                zkClient.start();
            }
            loadZkData();
            LOGGER.info("初始化ZK,载入ZK数据完成 props:{}", JSON.toJSONString(pros));
        }
    }


    @Override
    public String getConfig(String key) {
        String value = pros.getProperty(key);
        if (value != null) {
            return value;
        }

        value = System.getProperty(key);
        if (value != null) {
            return value;
        }
        try {
            return new String(zkClient.getData().forPath(DEFAULT_CONFIG_PATH));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public String getConfig(String key, long timeoutMills) {
        return getConfig(key);
    }


    @Override
    public boolean putConfig(String key, String content) {
        if(StringUtil.isEmpty(key) || StringUtil.isEmpty(content)){
            return false;
        }
        pros.setProperty(key, content);
        return true;
    }


    @Override
    public boolean putConfig(String key, String content, long timeoutMills) {
        pros.setProperty(key, content);
        try {
            zkClient.setData().forPath(DEFAULT_CONFIG_PATH, formatConfigStr());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public boolean removeConfig(String dataId, long timeoutMills) {
        pros.remove(dataId);
        try {
            zkClient.delete().forPath(DEFAULT_CONFIG_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean removeConfig(String key) {
        return false;
    }



    @Override
    public void addConfigListener(String key) {
        LOGGER.info("ZK添加监听器, key:{}", key);
        if (StringUtil.isBlank(key)) {
            return;
        }
        ZkListener zkListener = new ZkListener("/"+key);
        CONFIG_LISTENERS_MAP.put(key, zkListener);
        zkListener.onProcessEvent(new ConfigChangeEvent());
    }


    @Override
    public void removeConfigListener(String key) {
        if (StringUtil.isBlank(key)) {
            return;
        }
        LOGGER.info("ZK删除监听器, key:{}", key);
        ConfigChangeListener configChangeListeners = getConfigListeners(key);
        CONFIG_LISTENERS_MAP.remove(key);
        ZkListener zkListener = (ZkListener) configChangeListeners;
        zkListener.onShutDown();
    }


    @Override
    public ConfigChangeListener getConfigListeners(String key) {
        return CONFIG_LISTENERS_MAP.get(key);
    }


    @Override
    public Map<String, String> getConfigByPrefix(String prefix) {
        return null;
    }

    @Override
    public String getType() {
        return "zk";
    }




    static void loadZkData() throws Exception {

        byte[] bt = zkClient.getData().forPath(DEFAULT_CONFIG_PATH);
        if (bt != null && bt.length > 0){
            ByteArrayInputStream bArray = new ByteArrayInputStream(bt);
            pros.load(bArray);
        }
    }


    private static byte[] formatConfigStr() {
        StringBuilder sb = new StringBuilder();

        Enumeration<?> enumeration = pros.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            String property = pros.getProperty(key);
            sb.append(key).append("=").append(property).append("\n");
        }
        LOGGER.info("ZK更新配置文件:{}", sb.toString());
        return sb.toString().getBytes();
    }


}
