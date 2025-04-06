package com.bpm.example.demo5.configurator;

import lombok.Setter;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.engine.cfg.AbstractProcessEngineConfigurator;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.validation.ProcessValidator;

public class CustomProcessValidatorConfigurator extends AbstractProcessEngineConfigurator {
    @Setter
    protected ProcessValidator processValidator;

    @Override
    public void beforeInit(AbstractEngineConfiguration engineConfiguration) {
    }

    //在流程引擎创建之前，所有默认配置已经完成之后被调用
    @Override
    public void configure(AbstractEngineConfiguration engineConfiguration) {
        ProcessEngineConfigurationImpl processEngineConfiguration
                = (ProcessEngineConfigurationImpl)engineConfiguration;
        processEngineConfiguration.setProcessValidator(processValidator);
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
