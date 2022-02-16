package com.jd.platform.jlog.common.config;

import com.alibaba.nacos.api.exception.NacosException;
import com.jd.platform.jlog.common.model.CenterConfig;

import java.util.List;
import java.util.Map;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName AbsConfigCenter.java
 * @Description TODO
 * @createTime 2022年02月10日 20:33:00
 */
public interface IConfigCenter {


    /**
     * 构建config-center client
     * @param config
     * @throws Exception
     */
    IConfigCenter buildClient(CenterConfig config) throws Exception;

    /**
     * 存入key，value
     */
    void put(String key, String value);

    /**
     * 根据key，获取value
     */
    String get(String key) throws NacosException;


    /**
     * 删除
     * @param key
     */
    void delete(String key);


    /**
     * 获取指定前缀的所有key
     */
    List<String> getPrefixKey(String key);

}
