package com.jd.platform.jlog.zk;

import com.alibaba.fastjson.JSON;
import com.jd.platform.jlog.common.utils.CollectionUtil;
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
import java.util.Set;

import static com.jd.platform.jlog.zk.ZkConfigurator.*;


/**
 * @author didi
 */
public class ZkListener implements ConfigChangeListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(ZkListener.class);

    private NodeCache cache;

    private String path;

    public ZkListener(String path) {
        this.path = path;
        cache = new NodeCache(zkClient, path);
        try {
            cache.start(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cache.getListenable().addListener(() -> {
            String value = null;
            if(null!=cache.getCurrentData()){
                value = new String(cache.getCurrentData().getData());
            }
            onChangeEvent(null);
            System.out.println("=####======   "+value);
        });
    }



    @Override
    public void onShutDown(){
        LOGGER.info("ZK删除监听器");
        try {
            cache.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChangeEvent(ConfigChangeEvent event) {
        LOGGER.info("ZK数据变更-当前监听器关注的path:{} PROPERTIES:{}", path, JSON.toJSONString(PROPERTIES));
        Properties props = new Properties();
        props.putAll(PROPERTIES);
        try {
            LOGGER.info("ZK数据变更,旧Properties:{}", JSON.toJSONString(props));
            loadZkData();
            LOGGER.info("ZK数据变更,新Properties:{}", JSON.toJSONString(PROPERTIES));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Set<String> diffKeys = CollectionUtil.diffKeys(props, PROPERTIES);
        if(!diffKeys.isEmpty()){
            for (String diffKey : diffKeys) {
                LOGGER.warn("ZK {} 配置变更 key={} 变更事件:{}", path, diffKey,
                        new ConfigChangeEvent(diffKey,
                                String.valueOf(props.get(diffKey)),
                                String.valueOf(PROPERTIES.get(diffKey).toString())));
            }
        }
    }

}
