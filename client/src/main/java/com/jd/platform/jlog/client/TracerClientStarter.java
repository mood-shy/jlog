package com.jd.platform.jlog.client;

import com.jd.platform.jlog.client.task.Monitor;
import com.jd.platform.jlog.client.mdc.Mdc;
import com.jd.platform.jlog.client.udp.HttpSender;
import com.jd.platform.jlog.client.udp.UdpClient;
import com.jd.platform.jlog.client.udp.UdpSender;
import com.jd.platform.jlog.common.config.ConfigCenterFactory;
import com.jd.platform.jlog.common.model.CenterConfig;
import com.jd.platform.jlog.common.model.TagConfig;
import com.jd.platform.jlog.common.model.TagHandler;

/**
 * TracerClientStarter
 * @author wuweifeng
 * @version 1.0
 * @date 2021-08-13
 */
public class TracerClientStarter {
    /**
     * 机房
     */
    private Mdc mdc;


    private CenterConfig centerConfig;


    private TagConfig tagConfig;


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
        private Mdc mdc;
        private CenterConfig centerConfig;
        private TagConfig tagConfig;

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

        public Builder setCenterConfig(CenterConfig centerConfig) {
            this.centerConfig = centerConfig;
            return this;
        }

        public Builder setTagConfig(TagConfig tagConfig) {
            this.tagConfig = tagConfig;
            return this;
        }

        public TracerClientStarter build() {
            TracerClientStarter tracerClientStarter = new TracerClientStarter(appName);
            tracerClientStarter.centerConfig = centerConfig;
            tracerClientStarter.tagConfig = tagConfig;
            tracerClientStarter.mdc = mdc;
            return tracerClientStarter;
        }
    }

    /**
     * 启动监听etcd
     */
    public void startPipeline() throws Exception {
        //设置ConfigCenter
        ConfigCenterFactory.buildConfigCenter(centerConfig);
        System.out.println("tagconfig"+tagConfig.getDelimiter());
        TagHandler.buildTag(tagConfig);

        Context.MDC = mdc;

        Monitor starter = new Monitor();
        starter.start();

        UdpClient udpClient = new UdpClient();
        udpClient.start();

        //开启发送
        UdpSender.uploadToWorker();

        //开启大对象http发送
        HttpSender.uploadToWorker();
    }
}
