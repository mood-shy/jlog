package com.jd.platform.jlog.common.config.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.jd.platform.jlog.common.config.IConfigCenter;
import com.jd.platform.jlog.common.model.CenterConfig;

import java.util.List;
import java.util.Map;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName ApolloClient.java
 * @Description TODO
 * @createTime 2022年02月10日 20:37:00
 */


public class ApolloClient implements IConfigCenter {

    private Config config;

    public ApolloClient() {}

    public ApolloClient(Config config) {
        this.config = config;
    }

    @Override
    public IConfigCenter buildClient(CenterConfig centerConfig){
        config = ConfigService.getAppConfig();
        return new ApolloClient(config);
    }

    @Override
    public void put(String key, String value) {

    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public void delete(String key) {

    }

    @Override
    public List<String> getPrefixKey(String key) {
        return null;
    }


}
