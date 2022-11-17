package com.jd.platform.jlog.common.model;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * 客户端-服务端彼此传输的数据
 * @author wuweifeng
 * @version 1.0
 * @date 2021-08-17
 */
public class TracerData implements Serializable {
    /**
     * 多个tracer批量打包后
     */
    private List<TracerBean> tracerBeanList;

    //发送地址（仅多播时候使用）
    private transient InetSocketAddress address;

    public InetSocketAddress getAddress() { return address; }

    public void setAddress(InetSocketAddress address) { this.address = address; }

    public List<TracerBean> getTracerBeanList() {
        return tracerBeanList;
    }

    public void setTracerBeanList(List<TracerBean> tracerBeanList) {
        this.tracerBeanList = tracerBeanList;
    }

    @Override
    public String toString() {
        return "TracerData{" +
                "tracerBeanList=" + tracerBeanList +
                ", address=" + address +
                '}';
    }
}
