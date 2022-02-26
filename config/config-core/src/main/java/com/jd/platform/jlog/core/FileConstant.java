package com.jd.platform.jlog.core;

import java.util.HashSet;
import java.util.Set;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName FileConstant.java
 * @Description TODO
 * @createTime 2022年02月26日 10:19:00
 */
public class FileConstant {

    /**
     * 监听重读配置文件间隔 单位ms
     */
    static final long LISTENER_CONFIG_INTERVAL = 10000;

    static final String CONFIG_FILE_PROPERTIES = "/application.properties";

    static final String CONFIG_FILE_YML = "/application.yml";

    static final String JLOG_CONFIG_FILE = "/jLog.properties";

    static final String ENV = "env";

    static final long AWAIT_TIME = 3 * 1000;

    static final String YML = "yml";


    /**
     * 配置文件集合
     */
    public static final Set<String> CONFIG_FILES = new HashSet<String>() {
        {
            add(CONFIG_FILE_PROPERTIES);
            add(CONFIG_FILE_YML);
            add(JLOG_CONFIG_FILE);
        }
    };
}
