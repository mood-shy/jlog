package com.jd.platform.jlog.common.constant;

import java.util.HashSet;
import java.util.Set;

/**
 * 常量工具类Constant
 *
 * @author wuweifeng wrote on 2019-12-05
 * @version 1.0
 */
public class Constant {
    /**
     * netty通信端口
     */
    public static int NETTY_PORT = 9999;
    /**
     * 冒号
     */
    public static String SPLITER = ":";
    /**
     * 所有的workers，存这里
     */
    public static String WORKER_PATH = "/userTracer/workers/";

    /**
     * 当客户端要删除某个key时，就往etcd里赋值这个value，设置1秒过期，就算删除了
     */
    public static String DEFAULT_DELETE_VALUE = "#[DELETE]#";



    public static int TAG_NORMAL_KEY_MAX_LEN = 20;

    public static String TAG_NORMAL_KEY = "normal";

    public static final Set<String> SPECIAL_CHAR = new HashSet<String>() {
        {
            add("*");
            add(".");
            add("?");
            add("+");
            add("$");
            add("^");
            add("[");
            add("]");
            add("(");
            add(")");
            add("{");
            add("}");
            add("|");
        }
    };

}
