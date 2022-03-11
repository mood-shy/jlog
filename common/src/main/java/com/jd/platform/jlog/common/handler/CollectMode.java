package com.jd.platform.jlog.common.handler;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName CollectMode.java
 * @Description 定义采集模式的组合 用于以后method级别的tag注解
 * @createTime 2022年02月24日 23:10:00
 */

public class CollectMode {

    /**
     * 挂起 不提取不压缩
     */
    public static final long SUSPEND = 0L;

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
     * 提取resp
     */
    public static final long E_RESP = 1L << 5;

    /**
     * 压缩resp
     */
    public static final long C_RESP = 1L << 6;



    /**  =======================================  下面是组合  ======================================= */



    /**
     * 提取req+log
     */
    public static final long EXTRACT_REQ_LOG = E_REQ | E_LOG;

    /**
     * 提取req+resp
     */
    public static final long EXTRACT_REQ_REQP = E_REQ | E_RESP;

    /**
     * 提取req+resp
     */
    public static final long EXTRACT_REQ_RESP = E_REQ | E_RESP;

    /**
     * 提取req+log+resp
     */
    public static final long EXTRACT_ALL = E_REQ | E_LOG | E_RESP;


    /**
     * 压缩req+log
     */
    public static final long COMPRESS_REQ_LOG = C_REQ | C_LOG;

    /**
     * 压缩req+resp
     */
    public static final long COMPRESS_REQ_REQP = C_REQ | C_RESP;

    /**
     * 压缩log+resp
     */
    public static final long COMPRESS_LOG_RESP = C_REQ | C_RESP;

    /**
     * 压缩req+log+resp
     */
    public static final long COMPRESS_ALL = C_REQ | C_LOG | C_RESP;


    public static void main(String[] args) {
        System.out.println(isMatched(EXTRACT_REQ_LOG, EXTRACT_REQ_RESP));
    }
    public static boolean isMatched(long indicator, long position) {
        return (indicator & position) == position;
    }

}
 // curl --location --request POST 'http://10.96.98.110:8058/app/flow/getappraiselist' --header 'Content-Type: application/x-www-form-urlencoded' --data-urlencode 'token=DF9YcBVAaMiLCZtZEZCf6MunJX8I3jWGmQXwRzb-oQIszD1uAjEQgNG7fG1Gq5nx2I6nzBFygwSWn8ZIIKrV3h0hUb3ubUwlKYsuijCNNGE6WVVLCLOQ1uvoLTza6KrCDPJNJfn5RfgjQfgnw8vo7j6KqX-HC0dyCCu58bg974f1U-_CibQWrXlXC-FM8lWrqhZrvVYvCBcSQ7iSur8CAAD__w==' --data-urlencode 'cityID=55000116' --data-urlencode 'appVersion=2.0.28' --data-urlencode 'country=BR'