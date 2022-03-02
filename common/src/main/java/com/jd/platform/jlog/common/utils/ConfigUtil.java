package com.jd.platform.jlog.common.utils;

import com.jd.platform.jlog.common.constant.Constant;

import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName ConfigUtil.java
 * @Description TODO
 * @createTime 2022年02月13日 21:40:00
 */
public class ConfigUtil {


    private static final String SERVER_SUFFIX = "Server";

    public static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();



    public static String escapeExprSpecialWord(String str) {

        if(str != null && str.length() > 0){
            for (String s : Constant.SPECIAL_CHAR) {
                if (str.contains(s)) {
                    str = str.replace(s, "\\" + s);
                }
            }
        }
        return str;
    }



    public static String formatConfigStr(Properties properties) {

        StringBuilder sb = new StringBuilder();
        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            Object property = properties.get(key);
            if(property != null){
                property = String.valueOf(property);
            }
            sb.append(key).append("=").append(property).append("\n");
        }
        return sb.toString();
    }



    public static byte[] formatConfigByte(Properties properties) {
        return formatConfigStr(properties).getBytes();
    }


/*

    public static ConfigCenterEnum getCenter(CenterConfig config) throws Exception {
        Class<?> clz = config.getClass();
        Field[] fields = clz.getDeclaredFields();

        for (Field field : fields) {
            Method m = clz.getMethod("get" + getMethodName(field.getName()));
            String val = (String)m.invoke(config);
            if (val != null) {
                for (ConfigCenterEnum center : ConfigCenterEnum.values()) {
                    String fd = field.getName().replace(SERVER_SUFFIX, "");
                    if(center.name().equals(fd.toUpperCase())){
                        return center;
                    }
                }
            }
        }
        throw new Exception("Configuration center cannot be found");
    }
*/


    private static String getMethodName(String fieldName) {
        byte[] items = fieldName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }
}
