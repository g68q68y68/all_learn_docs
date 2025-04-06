package com.example.config;

import com.example.cache.RedisProcessDefinitionCache;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class ActivitiEngineConfiguration {

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
