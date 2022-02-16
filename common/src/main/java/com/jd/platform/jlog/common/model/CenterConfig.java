package com.jd.platform.jlog.common.model;

import com.alibaba.nacos.common.utils.StringUtils;
import com.jd.platform.jlog.common.config.ConfigCenterEnum;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName JConfig.java
 * @Description TODO
 * @createTime 2022年02月11日 23:11:00
 */
public class CenterConfig {

    private String etcdServer;

    private String nacosServer;

    private String zkServer;


    public String getEtcdServer() {
        return etcdServer;
    }

    public void setEtcdServer(String etcdServer) {
        this.etcdServer = etcdServer;
    }

    public String getNacosServer() {
        return nacosServer;
    }

    public void setNacosServer(String nacosServer) {
        this.nacosServer = nacosServer;
    }

    public String getZkServer() {
        return zkServer;
    }

    public void setZkServer(String zkServer) {
        this.zkServer = zkServer;
    }


    @Override
    public String toString() {
        return "CenterConfig{" +
                ", etcdServer='" + etcdServer + '\'' +
                ", nacosServer='" + nacosServer + '\'' +
                ", zkServer='" + zkServer + '\'' +
                '}';
    }
}
