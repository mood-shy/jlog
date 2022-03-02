package com.jd.platform.jlog.zk;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.jd.platform.jlog.common.utils.FastJsonUtils;
import com.jd.platform.jlog.common.utils.StringUtil;
import com.jd.platform.jlog.core.*;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jd.platform.jlog.common.utils.ConfigUtil.formatConfigByte;
import static com.jd.platform.jlog.core.Constant.DEFAULT_TIMEOUT;
import static com.jd.platform.jlog.core.Constant.SERVER_ADDR_KEY;
import static com.jd.platform.jlog.zk.ZkConstant.*;


/**
 * @author tangbohu
 */
public class ZkConfigurator implements Configurator {

    private final static Logger LOGGER = LoggerFactory.getLogger(ZkConfigurator.class);

    private static final Configurator FILE_CONFIG = ConfiguratorFactory.base;

    static volatile CuratorFramework zkClient;

    static volatile Properties PROPERTIES = new Properties();

    private static volatile ZkListener ZKLISTENER = null;



    public ZkConfigurator() throws Exception {
        System.out.println("### SERVER_ADDR_KEY ===> "+FILE_CONFIG.getConfig(SERVER_ADDR_KEY));
        if (zkClient == null) {
            synchronized (ZkConfigurator.class) {
                zkClient = CuratorFrameworkFactory.builder().connectString(FILE_CONFIG.getConfig(SERVER_ADDR_KEY))
                        // 连接超时时间
                        .sessionTimeoutMs(6000)
                        // 会话超时时间
                        .connectionTimeoutMs(2000)
                        .namespace(NAMESPACE)
                        // 刚开始重试间隔为1秒，之后重试间隔逐渐增加，最多重试不超过三次
                        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                        .build();
                zkClient.start();
            }

            if(zkClient.checkExists().forPath(DEFAULT_WORKER_PATH) == null){
                zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(DEFAULT_WORKER_PATH);
            }
            loadZkData();
            addConfigListener(DEFAULT_CONFIG_PATH);
            LOGGER.info("初始化ZK,载入ZK数据完成 props:{}", JSON.toJSONString(PROPERTIES));
        }
    }


    @Override
    public String getConfig(String key) {
        return getConfig(key, DEFAULT_TIMEOUT);
    }


    @Override
    public String getConfig(String key, long timeoutMills) {
        String value = PROPERTIES.getProperty(key);
        if (value != null) {
            return value;
        }

        value = System.getProperty(key);
        if (value != null) {
            return value;
        }
        try {
            loadZkData();
        } catch (Exception e) {
            return null;
        }
        return PROPERTIES.getProperty(key);
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
        try {
            zkClient.setData().forPath(DEFAULT_CONFIG_PATH, formatConfigByte(PROPERTIES));
        } catch (Exception e) {
            return false;
        }
        return true;
    }



    @Override
    public boolean removeConfig(String key) {
        return removeConfig(key, DEFAULT_TIMEOUT);
    }



    @Override
    public boolean removeConfig(String key, long timeoutMills) {
        PROPERTIES.remove(key);
        try {
            zkClient.setData().forPath(DEFAULT_CONFIG_PATH, formatConfigByte(PROPERTIES));
        } catch (Exception e) {
            return false;
        }
        return true;
    }




    @Override
    public void addConfigListener(String node) {
        if(!DEFAULT_CONFIG_PATH.equals(node)){
            throw new RuntimeException("no support");
        }
        LOGGER.info("ZK添加监听器, node:{}", node);
        ZKLISTENER = new ZkListener(node);
        ZKLISTENER.onProcessEvent(new ConfigChangeEvent());
    }


    @Override
    public void removeConfigListener(String node) {
        if(!DEFAULT_CONFIG_PATH.equals(node)){
            throw new RuntimeException("no support");
        }
        LOGGER.info("ZK删除监听器, node:{}", node);
        ZKLISTENER.onShutDown();
        ZKLISTENER = null;
    }


    @Override
    public List<String> getConfigByPrefix(String prefix) {
        try {
            String val = getConfig(prefix);
            return FastJsonUtils.toList(val,String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            PROPERTIES.load(bArray);
        }
        LOGGER.info("# loadZkData # PROPERTIES:{}",JSON.toJSONString(PROPERTIES));
    }

}
