package com.jd.platform.jlog.nacos;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.listener.AbstractSharedListener;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.common.utils.StringUtils;
import com.jd.platform.jlog.common.utils.CollectionUtil;
import com.jd.platform.jlog.core.ConfigChangeEvent;
import com.jd.platform.jlog.core.ConfigChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;


import static com.jd.platform.jlog.nacos.NacosConfigurator.CONFIG_LISTENER_MAP;
import static com.jd.platform.jlog.nacos.NacosConfigurator.props;
import static com.jd.platform.jlog.nacos.NacosConstant.DEFAULT_DATA_ID;
import static com.jd.platform.jlog.nacos.NacosConstant.JLOG_GROUP;


public class NacosListener extends AbstractSharedListener implements ConfigChangeListener, EventListener {


    private static final Logger LOGGER = LoggerFactory.getLogger(NacosConfigurator.class);

    private final String dataId;

    private final String group;


    public NacosListener(String dataId) {
        this.dataId = dataId;
        this.group = JLOG_GROUP;
    }

    @Override
    public void innerReceive(String dataId, String group, String configInfo) {

        LOGGER.info("configInfo:{}", configInfo);
        System.out.println("自带监听器" + configInfo);
        if (DEFAULT_DATA_ID.equals(dataId)) {
            Properties properties = new Properties();
            if (StringUtils.isNotBlank(configInfo)) {
                try {
                    properties.load(new StringReader(configInfo));
                } catch (IOException e) {
                    return;
                }
            }
            LOGGER.info("CONFIG_LISTENER_MAP :{}",JSON.toJSONString(CONFIG_LISTENER_MAP));
            ConfigChangeListener listener = CONFIG_LISTENER_MAP.get(dataId);
            boolean same = CollectionUtil.equals(properties, props);
            if(!same){
                ConfigChangeEvent event = new ConfigChangeEvent()
                        .setKey(DEFAULT_DATA_ID)
                        .setOldValue(JSON.toJSONString(props))
                        .setNewValue(JSON.toJSONString(properties))
                        .setNamespace(group);
                listener.onProcessEvent(event);
                props = properties;
            }
            return;
        }

        ConfigChangeEvent event = new ConfigChangeEvent().setKey(dataId).setNewValue(configInfo).setNamespace(group);
        this.onProcessEvent(event);
    }



    @Override
    public void onChangeEvent(ConfigChangeEvent event) {
        System.out.println("通用[配置]变更事件");
        LOGGER.info("通用[配置]变更事件 event:{}",event);
    }


    @Override
    public void onEvent(Event event) {
        System.out.println("通用[服务]事件");
        LOGGER.info("通用[服务]事件 event:{}",event);
    }
}
