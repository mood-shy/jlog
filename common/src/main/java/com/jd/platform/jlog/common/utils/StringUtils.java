package com.jd.platform.jlog.common.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author xiaochangbai
 * @date 2023-07-14 22:55
 */
public final class StringUtils {

    /**
     * 异常堆栈信息转string
     * @param e
     * @return
     */
    public static String errorInfoToString(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try{
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        } catch (Exception e2) {
            return "ErrorInfoFromException";
        }finally {
            if(sw!=null){
                try {
                    sw.close();
                } catch (IOException ex) {
                    return "ErrorInfoFromException";
                }
            }
            if(pw!=null){
                pw.close();
            }
        }
    }

}
