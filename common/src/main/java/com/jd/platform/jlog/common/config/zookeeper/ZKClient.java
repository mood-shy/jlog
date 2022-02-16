package com.jd.platform.jlog.common.config.zookeeper;


import com.jd.platform.jlog.common.config.IConfigCenter;
import com.jd.platform.jlog.common.model.CenterConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

import java.util.List;

/**
 * @author shenkaiwen5
 * @version 1.0
 * @date 2022-01-06
 */
public class ZKClient implements IConfigCenter {

    private CuratorFramework curator;

    public ZKClient() {}

    public ZKClient(CuratorFramework curatorFramework) {
        this.curator = curatorFramework;
    }


    @Override
    public IConfigCenter buildClient(CenterConfig config) {
        String zkServer = config.getZkServer();
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(zkServer)
                // 连接超时时间
                .sessionTimeoutMs(10000)
                // 会话超时时间
                .connectionTimeoutMs(10000)
                // 刚开始重试间隔为1秒，之后重试间隔逐渐增加，最多重试不超过三次
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
        return new ZKClient(client);
    }

    @Override
    public void put(String key, String value) {
        try {
            curator.create().creatingParentsIfNeeded() // 若创建节点的父节点不存在则先创建父节点再创建子节点
                    .withMode(CreateMode.EPHEMERAL) // 创建的是临时节点
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE) // 默认匿名权限,权限scheme id:'world,'anyone,:cdrwa
                    .forPath(key, value.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    public void delete(String key) { }

    @Override
    public String get(String key) {
        try {
            byte[] bt = curator.getData().forPath(key);
            return new String(bt, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<String> getPrefixKey(String key) {
        try {
            return curator.getChildren().forPath(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
