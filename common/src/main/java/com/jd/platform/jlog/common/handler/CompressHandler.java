package com.jd.platform.jlog.common.handler;

import com.jd.platform.jlog.common.utils.ZstdUtils;

import java.util.Base64;

import static com.jd.platform.jlog.common.handler.CollectMode.*;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName CompressHandler.java
 * @Description TODO
 * @createTime 2022年03月10日 20:52:00
 */
public class CompressHandler {

    /**
     * 压缩策略
     */
    private long compress = COMPRESS_LOG_RESP;

    private int limit = 10000;


    private CompressHandler(long compress, int limit){
        this.compress = compress;
        this.limit = limit;
    }

    private static volatile CompressHandler instance = null;

    public static void buildCompressHandler(long compress, int limit) {
        instance = new CompressHandler(compress,limit);
    }

    public static byte[] compressReq(byte[] contentBytes){
        if(instance == null || !isMatched(instance.compress, E_REQ)){ return contentBytes; }
        return doCompress(contentBytes);
    }

    public static byte[] compressLog(byte[] contentBytes){
        if(instance == null || !isMatched(instance.compress, E_LOG)){ return contentBytes; }
        return doCompress(contentBytes);
    }

    public static byte[] compressResp(byte[] contentBytes){
        if(instance == null || !isMatched(instance.compress, E_RESP)){ return contentBytes; }
        return doCompress(contentBytes);
    }

    private static byte[] doCompress(byte[] contentBytes){
        //最终的要发往worker的response，经历了base64压缩
        byte[] bytes = ZstdUtils.compress(contentBytes);
        return Base64.getEncoder().encode(bytes);
    }


    public long getCompress() {
        return compress;
    }

    public void setCompress(long compress) {
        this.compress = compress;
    }


}
