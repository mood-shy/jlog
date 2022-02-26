package com.jd.platform.jlog.core;

import java.util.Map;

/**
 * @author tangbohu
 * @version 1.0.0
 * @desc 参考log4J
 * @ClassName Configurator.java
 * @createTime 2022年02月15日 17:06:00
 */
public interface Configurator {

    /**
     * 获取配置
     * @param key key
     * @return val
     */
    String getConfig(String key);

    /**
     * 获取配置
     * @param key key
     * @param timeoutMills timeoutMills
     * @return val
     */
    String getConfig(String key, long timeoutMills);

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
     * 获取对应监听器
     * @param node key
     * @return Listeners
     */
    ConfigChangeListener getConfigListeners(String node);

    /**
     * 获取配置器类型
     * @return string example:apollo
     */
    String getType();

    /**
     * 根据前缀/父级路径获取子节点
     * @param prefix path
     * @return List
     */
    Map<String, String> getConfigByPrefix(String prefix);
}
