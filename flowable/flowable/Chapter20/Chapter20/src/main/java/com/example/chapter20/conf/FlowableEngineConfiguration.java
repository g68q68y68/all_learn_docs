package com.example.chapter20.conf;

import com.example.chapter20.engine.form.CustomFormEngine;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.form.FormEngine;
import org.flowable.form.engine.configurator.FormEngineConfigurator;
import org.flowable.form.spring.SpringFormEngineConfiguration;
import org.flowable.spring.ProcessEngineFactoryBean;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

@Configuration
public class FlowableEngineConfiguration {

    private final Logger logger = LoggerFactory.getLogger(FlowableEngineConfiguration.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean(name = "processEngine")
    public ProcessEngineFactoryBean processEngineFactoryBean() {
        ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
        factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
        return factoryBean;
    }

    @Bean(name = "processEngineConfiguration")
    public ProcessEngineConfigurationImpl processEngineConfiguration() {
        //初始化流程引擎配置
        SpringProcessEngineConfiguration processEngineConfiguration = new SpringProcessEngineConfiguration();
        processEngineConfiguration.setDataSource(dataSource);
        processEngineConfiguration.setDatabaseSchemaUpdate("true");
        processEngineConfiguration.setTransactionManager(transactionManager);

        //初始化表单引擎配置
        SpringFormEngineConfiguration formEngineConfiguration = new SpringFormEngineConfiguration();
        formEngineConfiguration.setDataSource(dataSource);
        formEngineConfiguration.setTransactionManager(transactionManager);
        formEngineConfiguration.setDatabaseSchemaUpdate("true");
        //自定义的属性

        //表单引擎配置器
        FormEngineConfigurator formEngineConfigurator = new FormEngineConfigurator();
        formEngineConfigurator.setFormEngineConfiguration(formEngineConfiguration);
        //注册到流程引擎配置中
        processEngineConfiguration.addConfigurator(formEngineConfigurator);
        //
//        CustomFormEngine formEngine = new CustomFormEngine();
//        Map<String,FormEngine> formEngines = new HashMap<>();
//        formEngines.put(formEngine.getName(),formEngine);
//        processEngineConfiguration.setFormEngines(formEngines);
        List<FormEngine> customFormEngines = Collections.singletonList(new CustomFormEngine());
        processEngineConfiguration.setCustomFormEngines(customFormEngines);
        return processEngineConfiguration;
    }

}
