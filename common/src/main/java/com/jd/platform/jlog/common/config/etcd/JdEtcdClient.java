package com.jd.platform.jlog.common.config.etcd;

import cn.hutool.core.collection.CollectionUtil;
import com.google.protobuf.ByteString;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.api.RangeResponse;
import com.ibm.etcd.client.EtcdClient;
import com.ibm.etcd.client.KvStoreClient;
import com.ibm.etcd.client.kv.KvClient;
import com.ibm.etcd.client.lease.LeaseClient;
import com.ibm.etcd.client.lock.LockClient;
import com.jd.platform.jlog.common.config.IConfigCenter;
import com.jd.platform.jlog.common.model.CenterConfig;

import java.util.List;
import java.util.Map;

/**
 * etcd客户端
 *
 * @author wuweifeng wrote on 2019-12-06
 * @version 1.0
 */
public class JdEtcdClient implements IConfigCenter {


    private KvClient kvClient;
    private LeaseClient leaseClient;
    private LockClient lockClient;

    public JdEtcdClient() {}

    public JdEtcdClient(KvStoreClient kvStoreClient) {
        this.kvClient = kvStoreClient.getKvClient();
        this.leaseClient = kvStoreClient.getLeaseClient();
        this.lockClient = kvStoreClient.getLockClient();
    }


    @Override
    public IConfigCenter buildClient(CenterConfig config){
        String etcdServer = config.getEtcdServer();
        return new JdEtcdClient(EtcdClient.forEndpoints(etcdServer).withPlainText().build());
    }

    @Override
    public void put(String key, String value) {
        kvClient.put(ByteString.copyFromUtf8(key), ByteString.copyFromUtf8(value)).sync();
    }

    @Override
    public String get(String key) {
        RangeResponse rangeResponse = kvClient.get(ByteString.copyFromUtf8(key)).sync();
        List<KeyValue> keyValues = rangeResponse.getKvsList();
        if (CollectionUtil.isEmpty(keyValues)) {
            return null;
        }
        return keyValues.get(0).getValue().toStringUtf8();
    }

    @Override
    public void delete(String key) {

    }

    @Override
    public List<String> getPrefixKey(String key) {
        return null;
    }

}
