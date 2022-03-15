package com.jd.platform.jlog.core;

import com.jd.platform.jlog.common.handler.CompressHandler;
import com.jd.platform.jlog.common.handler.ExtractHandler;
import com.jd.platform.jlog.common.handler.TagConfig;

import static com.jd.platform.jlog.common.constant.Constant.THRESHOLD;
import static com.jd.platform.jlog.common.handler.CollectMode.COMPRESS_LOG_RESP;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName ClientHandlerBuilder.java
 * @Description TODO
 * @createTime 2022年03月05日 22:07:00
 */
public class ClientHandlerBuilder {


    public static void buildHandler(TagConfig tagConfig, Configurator configurator){
        if(tagConfig == null){
            tagConfig = buildTagConfigByConfigurator(configurator);
        }
        ExtractHandler.buildExtractHandler(tagConfig);
        CompressHandler.buildCompressHandler(configurator.getLong("compress"), configurator.getLong("threshold"));
    }


    public static void refresh(){
        Configurator configurator = ConfiguratorFactory.getInstance();
        if(configurator == null){
            throw new RuntimeException("configurator is null");
        }
        ExtractHandler.refresh(buildTagConfigByConfigurator(configurator));
        CompressHandler.refresh(configurator.getLong("compress"), configurator.getLong("threshold") );
    }




    private static TagConfig buildTagConfigByConfigurator(Configurator configurator){
        return configurator.getObject("tag-config", TagConfig.class);
    }
}
