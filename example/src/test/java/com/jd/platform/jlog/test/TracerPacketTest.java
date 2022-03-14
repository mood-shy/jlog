package com.jd.platform.jlog.test;

import com.alibaba.fastjson.JSON;
import com.jd.platform.jlog.client.udp.UdpSender;
import com.jd.platform.jlog.clientdemo.ExampleApplication;
import com.jd.platform.jlog.common.model.TracerBean;
import com.jd.platform.jlog.common.utils.IpUtils;
import com.jd.platform.jlog.common.utils.ZstdUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 跳过过滤器，手动发送供worker消费的日志
 *
 * @author shenkaiwen5
 * @version 1.0
 * @date 2021-12-27
 */
//@SpringBootTest(classes = ExampleApplication.class)
//@RunWith(SpringRunner.class)
public class TracerPacketTest {

    //@Test
    public void testSendUdp() {
        TracerBean tracerBean = new TracerBean();
        List<Map<String, Object>> tracerObject = new ArrayList<>();
        tracerBean.setTracerObject(tracerObject);
        //将request信息保存
        Map<String, Object> requestMap = new HashMap<>(16);
        requestMap.put("tracerId", "1");
        requestMap.put("pin", UUID.randomUUID());
        requestMap.put("appName", "myTest");
        requestMap.put("uuid", "uuid" + UUID.randomUUID());
        requestMap.put("client", "android");
        requestMap.put("clientVersion", "10.3.2");
        requestMap.put("ip", "127.0.0.1");
        requestMap.put("serverIp", "127.0.0.1");
        requestMap.put("uri", "test");
        tracerObject.add(requestMap);

        //设置耗时
        tracerBean.setCostTime((int) (System.currentTimeMillis() - tracerBean.getCreateTime()));

        try {
            byte[] contentBytes = testString().getBytes(StandardCharsets.UTF_8);

            //最终的要发往worker的response
            byte[] bytes = ZstdUtils.compress(contentBytes);
            byte[] base64Bytes = Base64.encodeBase64(bytes);
            Map<String, Object> responseMap = new HashMap<>(8);
            responseMap.put("response", base64Bytes);

            tracerObject.add(responseMap);
            UdpSender.offerBean(tracerBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成测试的返回值
     */
    private String testString() {
        return JSON.toJSONString(new MyRes(IpUtils.getIp() + " send a test udp message."));
    }

    /**
     * 测试返回值类
     */
    class MyRes {
        String context;

        MyRes(String s) {
            this.context = s;
        }
    }
}
