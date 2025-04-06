package com.bpm.example.demo1.idm.engine.configurator;

import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.idm.engine.configurator.IdmEngineConfigurator;

public class CustomIdmEngineConfigurator extends IdmEngineConfigurator {

    @Override
    public void configure(AbstractEngineConfiguration engineConfiguration) {
        //初始化引擎配置
        initEngineConfigurations(engineConfiguration, idmEngineConfiguration);
        //启动身份管理引擎
        idmEngineConfiguration.buildIdmEngine();
        //初始化服务配置
        initServiceConfigurations(engineConfiguration, idmEngineConfiguration);
    }
}
