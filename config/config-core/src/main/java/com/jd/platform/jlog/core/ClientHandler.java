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
        return CompressHandler.compressReq(map);
    }


    public static Outcome processLog(String content){
        Map<String, Object> tagMap = ExtractHandler.extractLogTag(content);
        return new Outcome(tagMap, null);
    }


    public static Outcome processResp(byte[] resp, Map<String, Object> respMap){
        Map<String, Object> tagMap = ExtractHandler.extractRespTag(respMap);
        byte[] newContent = CompressHandler.compressResp(resp);
        return new Outcome(tagMap, newContent);
    }





   public static class Outcome{

        Map<String, Object> map;
        byte[] content;

        public Outcome(Map<String, Object> map, byte[] content) {
            this.map = map;
            this.content = content;
        }

       public Map<String, Object> getMap() {
           return map;
       }

       public void setMap(Map<String, Object> map) {
           this.map = map;
       }

       public byte[] getContent() {
           return content;
       }

       public void setContent(byte[] content) {
           this.content = content;
       }
   }
}
