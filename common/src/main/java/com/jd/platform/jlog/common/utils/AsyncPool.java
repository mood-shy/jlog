package com.jd.platform.jlog.common.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步线程池AsyncPool
 * @author wuweifeng
 * @version 1.0
 * @date 2020-07-10
 */
public class AsyncPool {
    private static ExecutorService threadPoolExecutor = Executors.newCachedThreadPool();

    public static void asyncDo(Runnable runnable) {
        threadPoolExecutor.submit(runnable);
    }

    public static void shutDown() {
        threadPoolExecutor.shutdown();
    }
}
