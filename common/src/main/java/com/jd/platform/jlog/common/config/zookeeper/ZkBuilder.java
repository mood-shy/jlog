package com.jd.platform.jlog.common.config.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author shenkaiwen5
 * @version 1.0
 * @date 2022-01-06
 */
public class ZkBuilder {
    /**
     * 构建ZKClient
     */
    public static ZKClient build(String endPoints) {
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(endPoints)
                // 连接超时时间
                .sessionTimeoutMs(1000)
                // 会话超时时间
                .connectionTimeoutMs(1000)
                // 刚开始重试间隔为1秒，之后重试间隔逐渐增加，最多重试不超过三次
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();

        return new ZKClient(client);
    }
}
