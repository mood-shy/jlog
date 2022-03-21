package com.jd.platform.jlog.common.handler;

import com.jd.platform.jlog.common.utils.ZstdUtils;
import java.util.Map;

import static com.jd.platform.jlog.common.constant.Constant.MIN;
import static com.jd.platform.jlog.common.constant.Constant.THRESHOLD;
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
    private long compress;

    /**
     * 超过limit的才压缩
     */
    private long threshold;


    private CompressHandler(long compress, long threshold){
        this.compress = compress;
        this.threshold = threshold;
    }


    private static volatile CompressHandler instance = null;


    public static void buildCompressHandler(Long compress, Long threshold) {
        if(compress == null || compress < 1){
            compress = COMPRESS_LOG_RESP;
        }
        if( threshold == null || threshold < MIN){
            threshold = THRESHOLD;
        }
        instance = new CompressHandler(compress, threshold);
    }

    public static void refresh(Long compress, Long threshold){
        instance = null;
        buildCompressHandler(compress, threshold);
    }

    public static Map<String, Object> compressReq(Map<String, Object> map){
        if(instance == null || !isMatched(instance.compress, C_REQ)){
            return  map;
        }

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            map.put(entry.getKey(), doCompress(entry.getValue().toString().getBytes()));
        }
        return map;
    }

    public static byte[] compressLog(byte[] contentBytes){
        if(instance == null || !isMatched(instance.compress, C_LOG)){ return contentBytes; }
        return doCompress(contentBytes);
    }

    public static byte[] compressResp(byte[] contentBytes){
        if(instance == null || !isMatched(instance.compress, C_RESP)){ return contentBytes; }
        return doCompress(contentBytes);
    }

    private static byte[] doCompress(byte[] contentBytes){
        if(contentBytes.length < instance.threshold){
            return contentBytes;
        }
        return ZstdUtils.compress(contentBytes);
    }


    public long getCompress() {
        return compress;
    }

    public void setCompress(long compress) {
        this.compress = compress;
    }

}
