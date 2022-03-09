package com.jd.platform.jlog.core;

import java.util.List;

/**
 * @author tangbohu
 * @version 1.0.0
 * @desc 参考log4J
 * @ClassName Configurator.java
 * @createTime 2022年02月15日 17:06:00
 */
public interface Configurator {

    /**
     * 获取string配置
     * @param key key
     * @return val
     */
    String getString(String key);


    /**
     * 获取LONG配置
     * @param key key
     * @return val
     */
    Long getLong(String key);

    /**
     * 获取LIST类型配置
     * @param key key
     * @return val
     */
    List<String> getList(String key);

    /**
     * 获取实体类型配置
     * @param key key
     * @return val
     */
    <T> T getObject(String key, Class<T> clz);


    /**
     * 设置配置
     * @param key key
     * @param content val
     * @return content val
     */
    boolean putConfig(String key, String content);

    /**
     * 设置配置
     * @param key key
     * @return content val
     * @param timeoutMills timeoutMills
     * @param content content
     */
    boolean putConfig(String key, String content, long timeoutMills);


    /**
     * 移除配置
     * @param key key
     * @return val
     */
    boolean removeConfig(String key);

    /**
     * 移除配置
     * @param key key
     * @param timeoutMills timeoutMills
     * @return val
     */
    boolean removeConfig(String key, long timeoutMills);


    /**
     * 根据前缀/父级路径获取子节点
     * @param prefix path
     * @return List
     */
    List<String> getConfigByPrefix(String prefix);


    /**
     * 添加监听器
     * @param node 文件
     */
    void addConfigListener(String node);


    /**
     * 移除监听器
     * @param node 节点 file or dir
     */
    void removeConfigListener(String node);


    /**
     * 获取配置器类型
     * @return string example:apollo
     */
    String getType();


}
