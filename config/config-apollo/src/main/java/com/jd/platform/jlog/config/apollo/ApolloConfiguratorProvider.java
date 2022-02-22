package com.jd.platform.jlog.config.apollo;

import com.jd.platform.jlog.core.Configurator;
import com.jd.platform.jlog.core.ConfiguratorProvider;

/**
 * @author didi
 */
public class ApolloConfiguratorProvider implements ConfiguratorProvider {
    @Override
    public Configurator build() {
        return ApolloConfigurator.getInstance();
    }
}

