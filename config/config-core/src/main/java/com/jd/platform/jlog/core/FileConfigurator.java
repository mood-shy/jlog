package com.jd.platform.jlog.core;

import com.jd.platform.jlog.common.utils.StringUtil;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;


/**
 * @author didi
 */
public class FileConfigurator implements Configurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileConfigurator.class);

    private static final long LISTENER_CONFIG_INTERVAL = 5000;

    private static final String CONFIG_FILE_PROPERTIES = "/application.properties";

    private static final String CONFIG_FILE_YML = "/application.yml";

    private static final String JLOG_CONFIG_FILE = "/jLog.properties";

    private static final String[] FILE_ARRAY = { CONFIG_FILE_PROPERTIES, CONFIG_FILE_YML, JLOG_CONFIG_FILE };

    private final ConcurrentMap<String, ConfigChangeListener> configListenerMap = new ConcurrentHashMap<>(8);

    private final Map<String, String> listenedConfigMap = new HashMap<>(8);

    private final FileListener fileListener = new FileListener();

    private final Properties properties = new Properties();

    private volatile long lastModify = 0L;


    public FileConfigurator() throws IOException {

        String env = System.getenv("env");

        for (String file : FILE_ARRAY) {

            file = StringUtil.isEmpty(env) ? file : file + "_" + env;

            try (InputStream fis = this.getClass().getResourceAsStream(file)) {
                if(fis != null){
                    properties.load(fis);
                    LOGGER.info("{}配置文件配置:{}", file, properties.toString());
                }else{
                    LOGGER.warn("{}配置文件为空",file);
                }
            }
        }
        LOGGER.info("合并后的配置:{}",properties.toString());
    }


    @Override
    public String getConfig(String key) {
        return properties.getProperty(key);
    }


    @Override
    public String getConfig(String key, long timeoutMills) {
        String value = System.getProperty(key);
        if (value != null) {
            return value;
        }

        if(properties.size() == 0){
            // 不正常 warn
            return null;
        }
       return properties.getProperty(key);
    }

    @Override
    public boolean putConfig(String key, String content) {
        return putConfig(key,content,2000L);
    }

    @Override
    public boolean putConfig(String key, String content, long timeoutMills) {
        return false;
    }


    @Override
    public boolean removeConfig(String key, long timeoutMills) {
        return false;
    }


    @Override
    public void addConfigListener(String key) {
        if (StringUtil.isBlank(key)) {
            return;
        }
        FileListener fileListener = new FileListener();
        configListenerMap.put(key, fileListener);
        listenedConfigMap.put(key, ConfiguratorFactory.getInstance().getConfig(key,1000L));
        fileListener.addListener(key, fileListener);
    }


    @Override
    public boolean removeConfig(String key) {
        return false;
    }



    @Override
    public void removeConfigListener(String key) {
        ConfigChangeListener configListener = getConfigListeners(key);
        configListenerMap.remove(key);
        listenedConfigMap.remove(key);
        configListener.onShutDown();
    }



    @Override
    public ConfigChangeListener getConfigListeners(String key) {
        return configListenerMap.get(key);
    }



    @Override
    public String getType() {
        return "file";
    }

    @Override
    public Map<String, String> getConfigByPrefix(String prefix) {
        return null;
    }



    class FileListener implements ConfigChangeListener {

        private final Map<String, Set<ConfigChangeListener>> keyListenersMap = new HashMap<>();

        private final ExecutorService executor = new ThreadPoolExecutor(1, 1, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                new DefaultThreadFactory("fileListener", 1));

        FileListener() {}

        public synchronized void addListener(String dataId, ConfigChangeListener listener) {
            if (keyListenersMap.isEmpty()) {
                fileListener.onProcessEvent(new ConfigChangeEvent());
            }

            keyListenersMap.computeIfAbsent(dataId, value -> new HashSet<>()).add(listener);
        }

        @Override
        public void onChangeEvent(ConfigChangeEvent event) {
            while (true) {
                for (String key : keyListenersMap.keySet()) {
                    try {
                        checkAndConfigure();
                        String currentConfig = properties.getProperty(key);
                        if (currentConfig != null) {
                            String oldConfig = listenedConfigMap.get(key);
                            if (currentConfig.equals(oldConfig)) {
                                listenedConfigMap.put(key, currentConfig);
                                event.setKey(key).setNewValue(currentConfig).setOldValue(oldConfig);

                                for (ConfigChangeListener listener : keyListenersMap.get(key)) {
                                    listener.onChangeEvent(event);
                                }
                            }
                        }
                    } catch (Exception exx) {
                        LOGGER.error("fileListener execute error, key :{}", key, exx);
                    }
                }
                try {
                    Thread.sleep(LISTENER_CONFIG_INTERVAL);
                } catch (InterruptedException e) {
                    LOGGER.error("fileListener thread sleep error:{}", e.getMessage());
                }
            }
        }

        @Override
        public ExecutorService getExecutorService() {
            return executor;
        }
    }




    private void checkAndConfigure() {

        File file;
        FileInputStream inputStream = null;

        try {
            file = new File(JLOG_CONFIG_FILE);

            long l = file.lastModified();

            if(l <= this.lastModify){
                return;
            }
            this.lastModify = l;
            inputStream = new FileInputStream(file);
            this.properties.clear();
            this.properties.load(inputStream);
            inputStream.close();
        } catch (Exception e) {
            if (e instanceof InterruptedIOException) {
                Thread.currentThread().interrupt();
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (InterruptedIOException oe) {
                    Thread.currentThread().interrupt();
                } catch (Throwable ignored) {

                }
            }
        }
    }



}
