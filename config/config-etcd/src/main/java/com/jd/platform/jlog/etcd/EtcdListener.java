package com.jd.platform.jlog.etcd;

import com.google.protobuf.ByteString;
import com.ibm.etcd.api.Event;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.client.EtcdClient;
import com.ibm.etcd.client.kv.KvClient;
import com.ibm.etcd.client.kv.WatchUpdate;
import com.jd.platform.jlog.core.ConfigChangeEvent;
import com.jd.platform.jlog.core.ConfigChangeListener;
import com.jd.platform.jlog.core.ConfigChangeType;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName EtcdListener.java
 * @Description TODO
 * @createTime 2022年02月21日 23:34:00
 */
public class EtcdListener implements ConfigChangeListener {
    private KvClient.WatchIterator iterator;
    private final ExecutorService executor = new ThreadPoolExecutor(1, 1, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
            new DefaultThreadFactory("etcdListener", 1));

    public EtcdListener(String node) {

        iterator = EtcdConfigurator.client.getKvClient().watch(ByteString.copyFromUtf8(node)).asPrefix().start();
        System.out.println("构造器EtcdListener"+node);

        getExecutorService().submit(() -> {
            while (iterator.hasNext()){
                try {
                    WatchUpdate update = iterator.next();
                    Event eve = update.getEvents().get(0);
                    KeyValue kv = eve.getKv();
                    Event.EventType eveType = eve.getType();
                    ConfigChangeType changeType = eveType.equals(Event.EventType.DELETE) ? ConfigChangeType.MODIFY : ConfigChangeType.DELETE;
                    ConfigChangeEvent event = new ConfigChangeEvent();
                    event.setKey(node).setNewValue(kv.getValue().toStringUtf8()).setChangeType(changeType);
                    onChangeEvent(event);
                }catch (RuntimeException e){
                    e.printStackTrace();
                }

            }
        });
    }


    @Override
    public void onShutDown() {
        this.iterator.close();
        getExecutorService().shutdownNow();
    }


    @Override
    public void onChangeEvent(ConfigChangeEvent event) {
        System.out.println("onChangeEvent 一次又一次的进入 ==> "+event.getKey());
    }

    @Override
    public ExecutorService getExecutorService() {
        return executor;
    }


}
