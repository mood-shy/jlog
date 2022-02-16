package com.jd.platform.jlog.common.config.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.jd.platform.jlog.common.config.IConfigCenter;
import com.jd.platform.jlog.common.model.CenterConfig;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName NacosClient.java
 * @Description TODO
 * @createTime 2022年02月10日 21:47:00
 */
public class NacosClient implements IConfigCenter {


    private ConfigService configService;

    public NacosClient(){}

    public NacosClient(ConfigService configService){
        this.configService = configService;
    }


    @Override
    public IConfigCenter buildClient(CenterConfig config) throws NacosException {
        String serverAddr = config.getNacosServer();
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        ConfigService configService = NacosFactory.createConfigService(properties);
        return new NacosClient(configService);
    }

    @Override
    public void put(String key, String value){
        try {
            boolean result = configService.publishConfig("data1", "group1", "nacosContentTEXT");
            System.out.println("publishConfig result==-> "+result);
        } catch (Exception e) {
            System.out.println("==-> "+e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public String get(String key) throws NacosException {
        return configService.getConfig("data1", "group1", 5000);
    }

    @Override
    public void delete(String key) {

    }

    @Override
    public List<String> getPrefixKey(String key) {
        return null;
    }


}
