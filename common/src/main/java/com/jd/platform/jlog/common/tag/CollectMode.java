package com.jd.platform.jlog.common.tag;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName CollectMode.java
 * @Description 定义采集模式的组合 用于以后method级别的tag注解
 * @createTime 2022年02月24日 23:10:00
 */

public class CollectMode {

    /**
     * 提取req
     */
    public static final long E_REQ = 1L;

    /**
     * 压缩req
     */
    public static final long C_REQ = 1L << 2;

    /**
     * 提取log
     */
    public static final long E_LOG = 1L << 3;

    /**
     * 压缩log
     */
    public static final long C_LOG = 1L << 4;

    /**
     * 提取log
     */
    public static final long E_RESP = 1L << 5;

    /**
     * 压缩log
     */
    public static final long C_RESP = 1L << 6;



    /**  =======================================  下面是组合  ======================================= */

    /**
     * 提取req，压缩req
     */
    public static final long E_REQ_C_REQ = E_REQ | C_REQ;

    /**
     * 提取log，压缩log
     */
    public static final long E_LOG_C_LOG = E_LOG | C_LOG;

    /**
     * 提取req+log，压缩req
     */
    public static final long E_REQ_E_LOG_C_REQ = E_REQ | E_LOG | C_REQ;

    /**
     * 提取req+log，压缩log
     */
    public static final long E_REQ_E_LOG_C_LOG = E_REQ | E_LOG | C_LOG;

    /**
     * 提取req+log，压缩req+log
     */
    public static final long E_REQ_E_LOG_C_REQ_E_LOG = E_REQ | E_LOG | C_REQ | E_LOG;


}
