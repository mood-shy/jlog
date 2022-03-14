package com.jd.platform.jlog.test;

import com.alibaba.fastjson.JSON;
import com.jd.platform.jlog.common.handler.TagConfig;
import com.jd.platform.jlog.core.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Random;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName Common.java
 * @Description TODO
 * @createTime 2022年03月01日 07:36:00
 */
public class Common {

    private final static Logger LOGGER = LoggerFactory.getLogger(Common.class);


    public static void getTest(Configurator configurator){

        LOGGER.info("配置器类型：{}", configurator.getType());
        String addr = configurator.getString("serverAddr");
        LOGGER.info("配置器get addr：{}", addr);
        TagConfig tagConfig = configurator.getObject("tagConfig", TagConfig.class);
        LOGGER.info("配置器get tagConfig：{}", tagConfig.toString());
        List workers = configurator.getList("workers");
        LOGGER.info("配置器get workers：{}", JSON.toJSONString(workers));
    }


    public static void modifyFile(String path)throws Exception{

        String temp;
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        StringBuffer buf = new StringBuffer();

        int id = new Random().nextInt(1000);
        int num = 0;
        // 保存该行前面的内容
        while ((temp = br.readLine()) != null) {
            if(num == 0){
                buf = buf.append("testKey: ").append(id);
            }else{
                buf = buf.append(temp);
            }
            num++;
            buf = buf.append(System.getProperty("line.separator"));
        }

        br.close();
        FileOutputStream fos = new FileOutputStream(file);
        PrintWriter pw = new PrintWriter(fos);
        pw.write(buf.toString().toCharArray());
        pw.flush();
        pw.close();
    }

}
