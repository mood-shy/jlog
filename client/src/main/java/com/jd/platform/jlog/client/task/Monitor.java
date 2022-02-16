package com.jd.platform.jlog.client.task;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.nacos.api.exception.NacosException;
import com.jd.platform.jlog.client.Context;
import com.jd.platform.jlog.client.mdc.Mdc;
import com.jd.platform.jlog.client.worker.WorkerInfoHolder;
import com.jd.platform.jlog.common.config.ConfigCenterEnum;
import com.jd.platform.jlog.common.config.ConfigCenterFactory;
import com.jd.platform.jlog.common.config.IConfigCenter;
import com.jd.platform.jlog.common.constant.Constant;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName Watchdog.java
 * @Description TODO
 * @createTime 2022年02月12日 10:20:00
 */
public class Monitor {
    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 开始获取workerIp地址并保存</>
     * 监听workerIp地址变化
     */
    public void start() {
        //fetchWorkerInfo();
    }

    /**
     * 每隔30秒拉取worker信息
     */
    private void fetchWorkerInfo() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //开启拉取etcd的worker信息，如果拉取失败，则定时继续拉取
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            logger.info("trying to connect to etcd and fetch worker info");
            fetch();

        }, 0, 30, TimeUnit.SECONDS);
    }

    /**
     * 从配置中心获取worker的ip集合
     */
    private void fetch() {
        IConfigCenter configCenter = ConfigCenterFactory.getClient(ConfigCenterEnum.ETCD);
        //获取所有worker的ip
        List<String> keys = null;
        try {
            //如果设置了机房属性，则拉取同机房的worker。如果同机房没worker，则拉取所有
            if (Context.MDC != null) {
                String mdc = parseMdc(Context.MDC);
                keys = configCenter.getPrefixKey(Constant.WORKER_PATH + Context.APP_NAME + "/" + mdc);
            }
            if (CollectionUtil.isEmpty(keys)) {
                keys = configCenter.getPrefixKey(Constant.WORKER_PATH + Context.APP_NAME);
            }

            //全是空，给个警告
            if (CollectionUtil.isEmpty(keys)) {
                logger.warn("very important warn !!! workers ip info is null!!!");
            }

            List<String> addresses = new ArrayList<>();
            if (keys != null) {
                for (String key : keys) {
                    //value里放的是ip地址
                    String ipPort = null;
                    try {
                        ipPort = configCenter.get(key);
                    } catch (NacosException e) {
                        e.printStackTrace();
                    }
                    addresses.add(ipPort);
                }
            }

            //将对应的worker保存下来
            WorkerInfoHolder.mergeAndConnectNew(addresses);
        } catch (StatusRuntimeException ex) {
            //etcd连不上
            logger.error("etcd connected fail. Check the etcd address!!!");
        }

    }

    /**
     * 解析mdc机房字符串
     */
    private String parseMdc(Mdc mdc) {
        switch (mdc) {
            case HT:
                return "ht";
            case LF:
                return "lf";
            case ZYX:
                return "zyx";
            case GZ:
                return "gz";
            case SH:
                return "sh";
            case SQ:
                return "sq";
            default:
                return null;
        }

    }
}
