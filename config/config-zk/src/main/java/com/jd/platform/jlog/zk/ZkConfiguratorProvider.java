package com.jd.platform.jlog.zk;


import com.jd.platform.jlog.core.Configurator;
import com.jd.platform.jlog.core.ConfiguratorProvider;


public class ZkConfiguratorProvider implements ConfiguratorProvider {
    @Override
    public Configurator build() {
        try {
            return new ZkConfigurator();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
