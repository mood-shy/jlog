/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.jd.platform.jlog.core;


import java.util.concurrent.*;


/**
 * @author didi
 */
public interface ConfigChangeListener {


    ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(1, 1,
        Integer.MAX_VALUE, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), Executors.defaultThreadFactory());


    void onChangeEvent(ConfigChangeEvent event);


    default void onProcessEvent(ConfigChangeEvent event) {
        getExecutorService().submit(() -> {
            beforeEvent();
            try {
                onChangeEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
            afterEvent();
        });
    }

    default void onShutDown() {
        getExecutorService().shutdownNow();
    }


    default ExecutorService getExecutorService() {
        return EXECUTOR_SERVICE;
    }


    default void beforeEvent() {

    }


    default void afterEvent() {

    }
}
