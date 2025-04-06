package com.bpm.example.demo1.idm.engine;

import org.flowable.idm.engine.IdmEngine;
import org.flowable.idm.engine.IdmEngineConfiguration;
import org.flowable.idm.engine.impl.IdmEngineImpl;

public class CustomIdmEngineConfiguration extends IdmEngineConfiguration {

    @Override
    public IdmEngine buildIdmEngine() {
        this.init();
        return new IdmEngineImpl(this);
    }

    @Override
    protected void init() {
        //初始化引擎配置
        super.initEngineConfigurations();
        //初始化命令上下文工厂
        super.initCommandContextFactory();
        //初始化命令执行器
        super.initCommandExecutors();
        //初始化身份管理相关服务
        super.initServices();
    }
}
