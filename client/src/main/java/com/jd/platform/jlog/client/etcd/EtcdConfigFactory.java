package com.jd.platform.jlog.client.etcd;


import com.jd.platform.jlog.common.config.IConfigCenter;
import com.jd.platform.jlog.common.config.etcd.JdEtcdBuilder;
import com.jd.platform.jlog.common.config.zookeeper.ZkBuilder;

/**
 * @author wuweifeng wrote on 2020-01-07
 * @version 1.0
 */
public class EtcdConfigFactory {
    private static IConfigCenter configCenter;

    private EtcdConfigFactory() {}

    public static IConfigCenter configCenter() {
        return configCenter;
    }

    public static void buildConfigCenter(String etcdServer) {
        //连接多个时，逗号分隔
        //configCenter = JdEtcdBuilder.build(etcdServer);
        configCenter = ZkBuilder.build(etcdServer);
    }
}
