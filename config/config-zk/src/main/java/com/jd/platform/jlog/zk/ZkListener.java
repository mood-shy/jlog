package com.jd.platform.jlog.zk;

import com.alibaba.fastjson.JSON;
import com.jd.platform.jlog.core.ConfigChangeEvent;
import com.jd.platform.jlog.core.ConfigChangeListener;
import com.jd.platform.jlog.core.ConfigChangeType;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static com.jd.platform.jlog.zk.ZkConfigurator.*;


/**
 * @author didi
 */
public class ZkListener implements ConfigChangeListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(ZkListener.class);

    private NodeCache cache;

    public ZkListener(String path) {
        cache = new NodeCache(zkClient, path);
        LOGGER.info("构造ZkListener:{}",path);
        try {
            cache.start(true);
        } catch (Exception e) {
            System.out.println("构造ZkListenereeeee"+e.getLocalizedMessage());
            e.printStackTrace();
        }
        cache.getListenable().addListener(() -> {
            String value = null;
            if(null!=cache.getCurrentData()){
                value = new String( cache.getCurrentData().getData());
            }
            System.out.println("=####======   "+value);
        });
        System.out.println("=####= SIZE=====   "+cache.getListenable().size());
    }



    @Override
    public void onShutDown(){
        LOGGER.info("ZK删除监听器, 开始的监听器list:{}",JSON.toJSONString( cache.getListenable()));
        try {
            cache.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("ZK删除监听器, 完成的监听器list:{}",JSON.toJSONString( cache.getListenable()));
    }

    @Override
    public void onChangeEvent(ConfigChangeEvent event) {
        LOGGER.info("ZK数据变更,通用事件触发onChangeEvent",event.toString());
      //  changeEvent();
    }



    private void changeEvent(){
        ChildData data = cache.getCurrentData();
        LOGGER.info("变更-当前数据cache:{}", cache);
        Properties propsTmp = pros;
        try {
            LOGGER.info("ZK数据变更,旧Properties:{}", JSON.toJSONString(propsTmp));
            loadZkData();
            LOGGER.info("ZK数据变更,新Properties:{}", JSON.toJSONString(pros));
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("ZK数据变更,CONFIG_LISTENERS_MAP:{}", JSON.toJSONString(CONFIG_LISTENERS_MAP));
        for (Map.Entry<String, ConfigChangeListener> entry : CONFIG_LISTENERS_MAP.entrySet()) {
            String listenedKey = entry.getKey();
            String propertyOld = pros.getProperty(listenedKey);
            String propertyNew = propsTmp.getProperty(listenedKey);
            if (!propertyOld.equals(propertyNew)) {
                ConfigChangeEvent event = new ConfigChangeEvent()
                        .setKey(listenedKey)
                        .setNewValue(propertyNew)
                        .setChangeType(ConfigChangeType.MODIFY);
                entry.getValue().onProcessEvent(event);
            }
        }
    }
}
