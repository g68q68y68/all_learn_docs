package com.bpm.example.demo1.cfg;

import lombok.Data;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.impl.interceptor.CommandInterceptor;
import org.flowable.common.spring.SpringTransactionInterceptor;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.transaction.PlatformTransactionManager;

@Data
public class CustomProcessEngineConfiguration extends ProcessEngineConfigurationImpl {
    //自定义扩展属性1：事务管理器
    protected PlatformTransactionManager customTransactionManager;

    //自定义扩展属性2：引擎类型
    protected String engineType;

    @Override
    public CommandInterceptor createTransactionInterceptor() {
        if (customTransactionManager == null) {
            throw new FlowableException("属性customTransactionManager不能为空");
        }
        return new SpringTransactionInterceptor(customTransactionManager);
    }
}

