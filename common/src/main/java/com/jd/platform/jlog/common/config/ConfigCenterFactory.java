package com.jd.platform.jlog.common.config;


import com.google.common.collect.Maps;
import com.jd.platform.jlog.common.config.apollo.ApolloClient;
import com.jd.platform.jlog.common.config.etcd.JdEtcdClient;
import com.jd.platform.jlog.common.config.nacos.NacosClient;
import com.jd.platform.jlog.common.config.zookeeper.ZKClient;
import com.jd.platform.jlog.common.model.CenterConfig;
import com.sun.deploy.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static com.jd.platform.jlog.common.utils.ConfigUtil.getCenter;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName ConfigCenterFactory.java
 * @Description TODO
 * @createTime 2022年02月10日 20:31:00
 */
public class ConfigCenterFactory {

    private static Map<ConfigCenterEnum, IConfigCenter> configCenterMap = Maps.newHashMap();


    public static void buildConfigCenter(CenterConfig config) throws Exception {

        ConfigCenterEnum center = getCenter(config);

        switch (center){
            case APOLLO:
                configCenterMap.put(center, new ApolloClient().buildClient(config));
                break;
            case NACOS:
                configCenterMap.put(center,new NacosClient().buildClient(config));
                break;
            case ETCD:
                configCenterMap.put(center, new JdEtcdClient().buildClient(config));
                break;
            case ZK:
                configCenterMap.put(center, new ZKClient().buildClient(config));
                break;
            default:
                System.out.println("Unsupported config center");
        }
    }


    public static IConfigCenter getClient(ConfigCenterEnum center) {
        return configCenterMap.get(center);
    }

}
