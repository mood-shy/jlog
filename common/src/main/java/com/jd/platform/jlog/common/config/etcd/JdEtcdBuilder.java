package com.jd.platform.jlog.common.config.etcd;

import com.ibm.etcd.client.EtcdClient;

/**
 * etcd连接器
 * @author wuweifeng wrote on 2019-12-10
 * @version 1.0
 */
public class JdEtcdBuilder {

    /**
     * @param endPoints 如https://127.0.0.1:2379 有多个时逗号分隔
     */
    public static JdEtcdClient build(String endPoints) {
        return new JdEtcdClient(EtcdClient.forEndpoints(endPoints).withPlainText().build());
    }
}
