package com.jd.platform.jlog.core;

import com.alibaba.fastjson.JSON;
import com.jd.platform.jlog.common.utils.StringUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName Node.java 包装类 用于以后的支持key->node->[listener,properties]
 * @createTime 2022年03月02日 20:37:00
 */
public class NodeBak implements Serializable {


    private String name;

    private ConfigChangeListener listener;

    private Properties properties;

    private String type;

    private Object val;


    static Properties newPro = new Properties();


    public static void main(String[] args) throws IOException {

        String path = "/Users/didi/Desktop/jlog/example/target/classes/application.properties";
      //  newPro.load();
        read(path);

        System.out.println(JSON.toJSONString(newPro));
    }

    protected static Map<String, String> toMap(Properties properties) {
        Map<String, String> result = new HashMap<>(10);
        Enumeration<?> propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String name = (String) propertyNames.nextElement();
            String value = properties.getProperty(name);
            result.put(name, value);
        }
        return result;
    }




    public static void read(String path) {

        FileReader fr = null;
        LineNumberReader lnr = null;
        String str;

        try {
            fr = new FileReader(path);
            lnr = new LineNumberReader(fr);

            Stack<String> stack = new Stack<>();
            HashMap<String, Object> tinyMap = new LinkedHashMap<>();
            List<Object> tinyList = new ArrayList<>();
            String temKey = "";
            while ((str = lnr.readLine()) != null) {
                if(StringUtil.isBlank(str) || !str.contains("=")){
                    continue;
                }
                String[] lineArr = str.split("=");
                String key = lineArr[0];
                String val = lineArr[1];

                if(!key.contains(".")){
                    newPro.put(key,val.trim());
                    continue;
                }

                String[] keyArr = key.split("\\.");

                String lastTinyKey = keyArr[keyArr.length - 1];
                if(lastTinyKey.contains("[") & lastTinyKey.contains("]")){
                    String[] lastArr = lastTinyKey.split("\\[");
                    for (int i = 0; i < keyArr.length - 1; i++) {
                        stack.push(keyArr[i]);
                    }
                    stack.push(lastArr[0]);

                  /*  if(!temKey.equals(lastArr[0])){
                        temKey = lastArr[0];
                        String out = stack.pop();
                        stack.push(temKey);
                    }*/
                    //list
                    tinyList.add(val);
                    newPro.put(keyArr[0],tinyList);
                }else{
                    for (int i = 0; i < keyArr.length - 1; i++) {
                        if(stack.empty() || !stack.peek().equals(keyArr[i])){
                            stack.push(keyArr[i]);
                        }
                    }

                   /* if(!temKey.equals(lastTinyKey)){
                        temKey = lastTinyKey;
                        String out = stack.pop();
                        System.out.println("out==> "+ out +"  temKey==> '"+temKey );
                        stack.push(temKey);
                    }*/
                    // map
                    tinyMap.put(keyArr[1],val.trim());
                    newPro.put(keyArr[0],tinyMap);
                }
                System.out.println("    --  stack==> " + JSON.toJSONString(stack) );

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (lnr != null) {
                try {
                    lnr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
