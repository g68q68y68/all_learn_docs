package com.example.config;

import com.example.cache.RedisProcessDefinitionCache;
import com.example.extend.CustomProcessDefinitionDataManagerImpl;
import com.example.extend.SnowFlakeIdGenerator;
import com.example.listeners.ProcessStartListener;
import com.example.listeners.TaskCreateListener;
import com.example.listeners.TaskToEsListener;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
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

import static org.flowable.common.engine.api.delegate.event.FlowableEngineEventType.*;

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
    private TaskToEsListener taskToEsListener;
    @Autowired
    private RedisProcessDefinitionCache processDefinitionCache;
    @Autowired
    private CustomProcessDefinitionDataManagerImpl processDefinitionDataManager;

    @Bean
    public ProcessEngine createProcessEngine() {
        SpringProcessEngineConfiguration engineConf = new SpringProcessEngineConfiguration();
        engineConf.setDataSource(dataSource);
        engineConf.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        engineConf.setTransactionManager(transactionManager);
        engineConf.setProcessDefinitionCache(processDefinitionCache);
        engineConf.setProcessDefinitionDataManager(processDefinitionDataManager);
        engineConf.setIdGenerator(new SnowFlakeIdGenerator());

        Map<String, List<FlowableEventListener>> eventListeners = new HashMap<>();
        eventListeners.put(PROCESS_STARTED.name(), Arrays.asList(processStartListener));
        eventListeners.put(TASK_CREATED.name(), Arrays.asList(taskCreateListener, taskToEsListener));
        eventListeners.put(TASK_COMPLETED.name(), Arrays.asList(taskToEsListener));
        eventListeners.put(TASK_ASSIGNED.name(), Arrays.asList(taskToEsListener));
        engineConf.setTypedEventListeners(eventListeners);
        return engineConf.buildProcessEngine();
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
