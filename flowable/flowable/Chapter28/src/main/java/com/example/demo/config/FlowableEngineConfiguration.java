package com.example.demo.config;

import com.example.demo.cache.RedisProcessDefinitionCache;
import com.example.demo.extend.CustomAsyncHistoryManager;
import com.example.demo.extend.MongoHistoricProcessInstanceDataManager;
import com.example.demo.extend.SnowFlakeIdGenerator;
import com.example.demo.mq.RocketMQMessageBasedJobManager;
import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.job.service.impl.asyncexecutor.AsyncJobExecutorConfiguration;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class FlowableEngineConfiguration {

    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private DataSource dataSource;

    @Autowired
    private RedisProcessDefinitionCache processDefinitionCache;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RocketMQMessageBasedJobManager jobManager;

    @Bean
    public ProcessEngine createProcessEngine() {
        SpringProcessEngineConfiguration engineConf = new SpringProcessEngineConfiguration();
        engineConf.setDataSource(dataSource);
        engineConf.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        engineConf.setTransactionManager(transactionManager);
        //指定自定义的流程定义缓存
        engineConf.setProcessDefinitionCache(processDefinitionCache);
        //使用自定义ID生成器
        engineConf.setIdGenerator(new SnowFlakeIdGenerator());
        //指定历史变量数据处理类
        engineConf.setHistoricProcessInstanceDataManager(
                new MongoHistoricProcessInstanceDataManager(engineConf, mongoTemplate));
        engineConf.setIdGenerator(new SnowFlakeIdGenerator());
        return engineConf.buildProcessEngine();
    }

//    @Bean
    public ProcessEngine createProcessEngine1() {
        SpringProcessEngineConfiguration engineConf = new SpringProcessEngineConfiguration();
        engineConf.setDataSource(dataSource);
        engineConf.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        engineConf.setTransactionManager(transactionManager);
        engineConf.setHistoryManager(new CustomAsyncHistoryManager(engineConf));

        engineConf.setAsyncHistoryExecutorActivate(false);
        engineConf.setAsyncHistoryEnabled(true);
        engineConf.setAsyncHistoryExecutorMessageQueueMode(true);
        AsyncJobExecutorConfiguration jobConf = new AsyncJobExecutorConfiguration();
        jobConf.setMaxAsyncJobsDuePerAcquisition(100);
        jobConf.setGlobalAcquireLockEnabled(true);
        engineConf.setAsyncHistoryExecutorConfiguration(jobConf);
        engineConf.setAsyncHistoryJsonGroupingEnabled(true);
        engineConf.setAsyncHistoryJsonGroupingThreshold(3);
        engineConf.setAsyncHistoryExecutorCorePoolSize(20);
        engineConf.setAsyncHistoryExecutorMaxPoolSize(20);
        engineConf.setAsyncExecutorMessageQueueMode(true);
        engineConf.setJobManager(jobManager);
        //指定自定义流程定义缓存
        engineConf.setProcessDefinitionCache(processDefinitionCache);
        //使用自定义ID生成器
        engineConf.setIdGenerator(new SnowFlakeIdGenerator());
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
