package com.jd.platform.jlog.test;

import com.alibaba.fastjson.JSON;
import com.jd.platform.jlog.clientdemo.ExampleApplication;
import com.jd.platform.jlog.core.Configurator;
import com.jd.platform.jlog.core.ConfiguratorFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import java.util.Random;

import static com.jd.platform.jlog.test.Common.getTest;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName ZKConfiguratorTest.java
 * @Description TODO
 * @createTime 2022年03月01日 07:35:00
 */
@SpringBootTest(classes = ExampleApplication.class)
@RunWith(SpringRunner.class)
public class ZKConfiguratorTest {



    private final static Logger LOGGER = LoggerFactory.getLogger(ZKConfiguratorTest.class);


    private Configurator configurator = null;



    @Before
    public void init() {
        configurator = ConfiguratorFactory.getInstance();
        getTest(configurator);
    }

    @Test
    public void testUpdateCFG() throws Exception {
        List<String> workers = configurator.getList("workers");
        LOGGER.info("初始化的workers：{}", JSON.toJSONString(workers));
        String myIp = "121.0";
        if(workers.contains(myIp)){
            LOGGER.info("自己的IP还在配置list里 什么也不做");
            return;
        }else{
            LOGGER.info("自己的IP不在配置list里 添加进去并发布");
            workers.add(myIp);
        }
        configurator.putConfig("workers",JSON.toJSONString(workers));
        List<String> workers2 = configurator.getList("workers");
        LOGGER.info("最新的workers：{}", JSON.toJSONString(workers2));
    }

    @Test
    public void testAddConfigListener() throws Exception {
        int i1 = new Random().nextInt(2000);
        int i2 = new Random().nextInt(2000);

        String val1 = configurator.getString("testKey");
        LOGGER.info("初始化的testKey的val:{}", val1);
        LOGGER.info("添加监听器后， 修改配置testKey = {}", i1);
       // configurator.putConfig("testKey",i1 + "");
        LOGGER.info("修改完毕 准备触发监听器");
        Thread.sleep(1000);
        String val2 = configurator.getString("testKey");
        LOGGER.info("第一次修改后的的val:{}", val2);
        Thread.sleep(1000);
    }
}
