package com.example.integration.config;

import org.flowable.common.engine.impl.interceptor.CommandInterceptor;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "flowable")
public class SpringbootProcessEngineConfiguration extends ProcessEngineConfigurationImpl {

    @Override
    public CommandInterceptor createTransactionInterceptor() {
        return null;
    }
}
