package com.jd.platform.jlog.core;

import com.jd.platform.jlog.common.handler.TagConfig;
import com.jd.platform.jlog.common.handler.TagHandler;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName TagHandlerBuilder.java
 * @Description TODO
 * @createTime 2022年03月05日 22:07:00
 */
public class TagHandlerBuilder {



    public static void buildTagHandler(TagConfig tagConfig, Configurator configurator){
        if(tagConfig == null){
            tagConfig = buildTagConfigByConfigurator(configurator);
        }
        TagHandler.buildHandler(tagConfig);
    }


    public static void refresh(){
        Configurator configurator = ConfiguratorFactory.getInstance();
        if(configurator == null){
            throw new RuntimeException("configurator is null");
        }
        TagHandler.refresh(buildTagConfigByConfigurator(configurator));
    }




    private static TagConfig buildTagConfigByConfigurator(Configurator configurator){
        return configurator.getObject("handler-config", TagConfig.class);
    }
}
