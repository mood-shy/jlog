package com.jd.platform.jlog.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName ZkTest.java
 * @Description TODO
 * @createTime 2022年02月22日 17:26:00
 */
public class ZkTest {
    private final static Logger log = LoggerFactory.getLogger(ZkTest.class);


    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString("101.42.242.201:2181")
                // 连接超时时间
                .sessionTimeoutMs(2000)
                // 会话超时时间
                .connectionTimeoutMs(6000)
              //  .namespace("jLog")
                // 刚开始重试间隔为1秒，之后重试间隔逐渐增加，最多重试不超过三次
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        //开启客户端
        client.start();
        //创建缓存节点
        NodeCache nodeCache = new NodeCache(client, "/jLog/jLog.properties");

        //将该节点数据初始化到本地缓存
        nodeCache.start(true);
        //添加节点监听事件,NodeCacheListener每次都会触发，但不能获取监听的操作类型到底是添加还是删除等。
        nodeCache.getListenable().addListener(() -> {
            String value = null;
            System.out.println("Listener-name: "+Thread.currentThread().getName());
            if(null!=nodeCache.getCurrentData()){
                value = new String(nodeCache.getCurrentData().getData());
            }
            System.out.println("=======   "+value);
        });


       client.setData().forPath("/jLog/jLog.properties","noweeee".getBytes());
        //睡眠等待监听事件触发
        Thread.sleep(25000);
        System.out.println("main-name: "+Thread.currentThread().getName());

        //关闭
        nodeCache.close();
        client.close();

    }

}
