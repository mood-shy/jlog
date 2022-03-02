package com.jd.platform.jlog.test;

import com.alibaba.fastjson.JSON;
import com.jd.platform.jlog.clientdemo.ClientDemoApplication;
import com.jd.platform.jlog.core.Configurator;
import com.jd.platform.jlog.core.ConfiguratorFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import static com.jd.platform.jlog.test.Common.getTest;
import static com.jd.platform.jlog.test.Common.modifyFile;


/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName FileConfiguratorTest.java
 * @Description TODO
 * @createTime 2022年02月28日 19:45:00
 */
@SpringBootTest(classes = ClientDemoApplication.class)
@RunWith(SpringRunner.class)
public class FileConfiguratorTest {


    private final static Logger LOGGER = LoggerFactory.getLogger(FileConfiguratorTest.class);


    private Configurator configurator = null;



    @Before
    public void init() {
        configurator = ConfiguratorFactory.getInstance();
        getTest(configurator);
    }



    @Test
    public void testAddConfigListener() throws Exception {
        configurator.addConfigListener("/application.yml");
        String path = "/Users/didi/Desktop/jlog/clientdemo/target/classes/application.yml";
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(new File(path));
        if (path.contains("yml")) {
            props.putAll(new Yaml().loadAs(fis, Map.class));
        } else {
            props.load(fis);
        }
        LOGGER.info("读取文件：{}最新配置:{}", path, JSON.toJSONString(props));
        modifyFile(path);
        LOGGER.info("修改文件完毕 准备触发监听器");
        Thread.sleep(1000);
        LOGGER.info("睡醒了 应该更新了配置，testKey：{}",configurator.getConfig("testKey"));
        LOGGER.info("移除监听器");
        configurator.removeConfigListener("/application.yml");
        modifyFile(path);
        LOGGER.info("修改文件完毕 准备验证监听器是否停止  最新testKey={}", configurator.getConfig("testKey"));
    }



}
