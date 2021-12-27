package com.jd.platform.jlog.client;

import com.jd.platform.jlog.client.etcd.EtcdConfigFactory;
import com.jd.platform.jlog.client.etcd.EtcdStarter;
import com.jd.platform.jlog.client.mdc.Mdc;
import com.jd.platform.jlog.client.udp.HttpSender;
import com.jd.platform.jlog.client.udp.UdpClient;
import com.jd.platform.jlog.client.udp.UdpSender;

/**
 * TracerClientStarter
 * @author wuweifeng
 * @version 1.0
 * @date 2021-08-13
 */
public class TracerClientStarter {
    /**
     * etcd地址
     */
    private String etcdServer;
    /**
     * 机房
     */
    private Mdc mdc;

    /**
     * TracerClientStarter
     */
    private TracerClientStarter(String appName) {
        if (appName == null) {
            throw new NullPointerException("APP_NAME cannot be null!");
        }
        Context.APP_NAME = appName;
    }

    public static class Builder {
        private String appName;
        private String etcdServer;
        private Mdc mdc;

        public Builder() {
        }

        public Builder setAppName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder setMdc(Mdc mdc) {
            this.mdc = mdc;
            return this;
        }

        public Builder setEtcdServer(String etcdServer) {
            this.etcdServer = etcdServer;
            return this;
        }

        public TracerClientStarter build() {
            TracerClientStarter tracerClientStarter = new TracerClientStarter(appName);
            tracerClientStarter.etcdServer = etcdServer;
            tracerClientStarter.mdc = mdc;

            return tracerClientStarter;
        }
    }

    /**
     * 启动监听etcd
     */
    public void startPipeline() {
        //设置ConfigCenter
        EtcdConfigFactory.buildConfigCenter(etcdServer);

        Context.MDC = mdc;

        EtcdStarter starter = new EtcdStarter();
        //与etcd相关的监听都开启
        starter.start();

        UdpClient udpClient = new UdpClient();
        udpClient.start();

        //开启发送
        UdpSender.uploadToWorker();

        //开启大对象http发送
        HttpSender.uploadToWorker();
    }
}
