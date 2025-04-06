package com.bpm.example.demo1;

import com.bpm.example.demo1.cfg.CustomProcessEngineConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.junit.Test;

@Slf4j
public class RunCustomProcessEngineConfigurationDemo {
    @Test
    public void runCustomProcessengineconfigurationDemo() throws Exception {
        //创建工作流引擎配置
        CustomProcessEngineConfiguration processEngineConfiguration
                = (CustomProcessEngineConfiguration) ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("flowable" +
                        ".custom-processengineconfiguration.xml");
        //创建工作流引擎
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        //获取自定义属性
        log.info("engineType：{}", ((CustomProcessEngineConfiguration) processEngine
                .getProcessEngineConfiguration()).getEngineType());
        log.info("customTransactionManager：{}",
                ((CustomProcessEngineConfiguration)processEngine
                        .getProcessEngineConfiguration()).getCustomTransactionManager()
                        .getClass().getName());
    }
}
