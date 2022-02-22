package com.jd.platform.jlog.nacos;

import com.alibaba.nacos.api.config.listener.AbstractSharedListener;
import com.alibaba.nacos.common.utils.StringUtils;
import com.jd.platform.jlog.core.ConfigChangeEvent;
import com.jd.platform.jlog.core.ConfigChangeListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

import static com.jd.platform.jlog.nacos.NacosConfigurator.CONFIG_LISTENERS_MAP;
import static com.jd.platform.jlog.nacos.NacosConfigurator.props;
import static com.jd.platform.jlog.nacos.NacosConstant.DEFAULT_DATA_ID;

public class NacosListener extends AbstractSharedListener {
        private final String dataId;
        private final ConfigChangeListener listener;

        /**
         * Instantiates a new Nacos listener.
         *
         * @param dataId   the data id
         * @param listener the listener
         */
        public NacosListener(String dataId, ConfigChangeListener listener) {
            this.dataId = dataId;
            this.listener = listener;
        }

        /**
         * Gets target listener.
         *
         * @return the target listener
         */
        public ConfigChangeListener getTargetListener() {
            return this.listener;
        }

        @Override
        public void innerReceive(String dataId, String group, String configInfo) {
            //The new configuration method to puts all configurations into a dateId
            if (DEFAULT_DATA_ID.equals(dataId)) {
                Properties seataConfigNew = new Properties();
                if (StringUtils.isNotBlank(configInfo)) {
                    try (Reader reader = new InputStreamReader(new ByteArrayInputStream(configInfo.getBytes()), StandardCharsets.UTF_8)) {
                        seataConfigNew.load(reader);
                    } catch (IOException e) {
                      //  LOGGER.error("load config properties error", e);
                        return;
                    }
                }

                //Get all the monitored dataids and judge whether it has been modified
                for (Map.Entry<String, ConcurrentMap<ConfigChangeListener, NacosListener>> entry : CONFIG_LISTENERS_MAP.entrySet()) {
                    String listenedDataId = entry.getKey();
                    String propertyOld = props.getProperty(listenedDataId, "");
                    String propertyNew = seataConfigNew.getProperty(listenedDataId, "");
                    if (!propertyOld.equals(propertyNew)) {
                        ConfigChangeEvent event = new ConfigChangeEvent()
                                .setKey(listenedDataId)
                                .setNewValue(propertyNew)
                                .setNamespace(group);

                        ConcurrentMap<ConfigChangeListener, NacosListener> configListeners = entry.getValue();
                        for (ConfigChangeListener configListener : configListeners.keySet()) {
                            configListener.onProcessEvent(event);
                        }
                    }
                }

                props = seataConfigNew;
                return;
            }

            //Compatible with old writing
            ConfigChangeEvent event = new ConfigChangeEvent().setKey(dataId).setNewValue(configInfo)
                    .setNamespace(group);
            listener.onProcessEvent(event);
        }
    }
