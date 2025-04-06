package com.example.config;

import com.example.cache.RedisProcessDefinitionCache;
import com.example.extend.CustomProcessDefinitionDataManagerImpl;
import com.example.listeners.ProcessStartListener;
import com.example.listeners.TaskCreateListener;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.*;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.flowable.common.engine.api.delegate.event.FlowableEngineEventType.PROCESS_STARTED;
import static org.flowable.common.engine.api.delegate.event.FlowableEngineEventType.TASK_CREATED;

@Configuration
public class FlowableEngineConfiguration {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private ProcessStartListener processStartListener;

    @Autowired
    private TaskCreateListener taskCreateListener;

    @Autowired
    private RedisProcessDefinitionCache processDefinitionCache;

    @Autowired
    private CustomProcessDefinitionDataManagerImpl processDefinitionDataManager;

    @Bean
    public SpringProcessEngineConfiguration processEngine() {
        SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
        configuration.setDataSource(dataSource);
        configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        configuration.setTransactionManager(transactionManager);
        //自定义流程定义缓存类
        configuration.setProcessDefinitionCache(processDefinitionCache);
        //自定义流程定义数据管理类
        configuration.setProcessDefinitionDataManager(processDefinitionDataManager);

        Map<String, List<FlowableEventListener>> eventListeners = new HashMap<>();
        eventListeners.put(PROCESS_STARTED.name(), Arrays.asList(processStartListener));
        eventListeners.put(TASK_CREATED.name(), Arrays.asList(taskCreateListener));
        configuration.setTypedEventListeners(eventListeners);
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
