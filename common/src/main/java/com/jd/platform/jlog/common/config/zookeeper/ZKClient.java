package com.jd.platform.jlog.common.config.zookeeper;

import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.client.kv.KvClient;
import com.jd.platform.jlog.common.config.IConfigCenter;
import org.apache.curator.framework.CuratorFramework;
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

    public ZKClient(CuratorFramework curatorFramework) {
        this.curator = curatorFramework;
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
    public void put(String key, String value, long leaseId) {

    }

    @Override
    public void revoke(long leaseId) {

    }

    @Override
    public long putAndGrant(String key, String value, long ttl) {
        return 0;
    }

    @Override
    public long setLease(String key, long leaseId) {
        return 0;
    }

    @Override
    public void delete(String key) {

    }

    @Override
    public String get(String key) {
        try {
            return curator.getData().forPath(key).toString();
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

    @Override
    public KvClient.WatchIterator watch(String key) {
        return null;
    }

    @Override
    public KvClient.WatchIterator watchPrefix(String key) {
        return null;
    }

    @Override
    public long keepAlive(String key, String value, int frequencySecs, int minTtl) throws Exception {
        return 0;
    }

    @Override
    public long buildAliveLease(int frequencySecs, int minTtl) throws Exception {
        return 0;
    }

    @Override
    public long buildNormalLease(long ttl) {
        return 0;
    }

    @Override
    public long timeToLive(long leaseId) {
        return 0;
    }

    @Override
    public KeyValue getKv(String key) {
        return null;
    }
}
