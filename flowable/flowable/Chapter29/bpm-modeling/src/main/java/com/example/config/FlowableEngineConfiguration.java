package com.example.config;

import com.example.cache.RedisProcessDefinitionCache;
import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class FlowableEngineConfiguration {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private RedisProcessDefinitionCache processDefinitionCache;

    @Bean(name = "processEngineConfiguration")
    public ProcessEngineConfigurationImpl processEngineConfiguration(DataSource dataSource) {
        SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
        configuration.setDataSource(dataSource);
        configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        configuration.setTransactionManager(transactionManager);
        configuration.setProcessDefinitionCache(processDefinitionCache);
        return configuration;
    }

    @Bean(name = "processEngine")
    public ProcessEngine processEngine(ProcessEngineConfiguration configuration) {
        return configuration.buildProcessEngine();
    }

    @Bean
    public TaskService taskService(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }

    @Bean
    public RuntimeService runtimeService(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    @Bean
    public RepositoryService repositoryService(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    @Bean
    public ManagementService managementService(ProcessEngine processEngine) {
        return processEngine.getManagementService();
    }
}
