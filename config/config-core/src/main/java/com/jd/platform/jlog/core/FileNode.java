package com.jd.platform.jlog.core;

import com.alibaba.fastjson.JSON;
import com.jd.platform.jlog.common.handler.CollectMode;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName FileNode.java
 * @createTime 2022年03月08日 16:32:00
 */
public final class FileNode {

    private String fillPath;

    private long lastModity;

    public static void main(String[] args) throws IOException {

        System.out.println(CollectMode.COMPRESS_ALL);
        System.out.println(CollectMode.EXTRACT_ALL);


        System.out.println();
        JcProperties properties = new JcProperties();
        String path = "/Users/didi/Desktop/jlog/example/target/classes/application.properties";
        properties.load(new FileInputStream(path));
        System.out.println(JSON.toJSONString(properties));
        Server bean = properties.getBean("server", Server.class);
        System.out.println(JSON.toJSONString(bean));
    }
}
