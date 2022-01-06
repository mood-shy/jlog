package com.jd.platform.jlog.dashboard;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * 设置组件启动及初始化
 * @author shenkaiwen5
 * @version 1.0
 * @date 2021-11-09
 */
@Configuration
@Data
public class Starter {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * fake
     */
    public static DelayQueue queue = new DelayQueue();

    /**
     * 定时调度线程池
     */
    private ScheduledExecutorService service = Executors.newScheduledThreadPool(8);

    @PostConstruct
    public void start() {

        new Thread(()->{
            while (true) {
                try {
                    queue.poll(125, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
