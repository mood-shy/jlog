package com.jd.platform.jlog.common.config.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperTest {
    public static void main(String[] args) throws Exception {
        ZooKeeper zk = new ZooKeeper("101.42.242.201:2181", 3000, null);

        System.out.println(zk.getClass());
        if(zk.exists("/test22", false) == null)
        {
            zk.create("/test22", "znode1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        System.out.println(new String(zk.getData("/test22", false, null)));
    }
    
}
