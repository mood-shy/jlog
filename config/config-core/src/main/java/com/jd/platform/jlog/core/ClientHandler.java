package com.jd.platform.jlog.core;

import com.jd.platform.jlog.common.handler.CompressHandler;
import com.jd.platform.jlog.common.handler.ExtractHandler;
import java.util.Map;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName ClientHandler.java
 * @createTime 2022年03月13日 16:53:00
 */
public class ClientHandler {

    public static Map<String, Object> processReq(Map<String, Object> reqMap){
        Map<String, Object> map = ExtractHandler.extractReqTag(reqMap);
        return CompressHandler.compressReq(map);;
    }


    public static Map<String, Object> processLog(){

        return null;
    }


    public static Map<String, Object> processResp(byte[] resp, String content){
        byte[] contentBytes
        return 1;
    }
}
