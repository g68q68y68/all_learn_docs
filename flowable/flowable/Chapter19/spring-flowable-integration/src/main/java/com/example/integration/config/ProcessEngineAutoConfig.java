package com.example.integration.config;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Value;

//@Configuration
public class ProcessEngineAutoConfig {

    @Value("${flowable.jdbcUrl}")
    private String jdbcUrl;
    @Value("${flowable.jdbcDriver}")
    private String jdbcDriver;
    @Value("${flowable.jdbcUsername}")
    private String jdbcUsername;
    @Value("${flowable.jdbcPassword}")
    private String jdbcPassword;
    @Value("${flowable.databaseSchemaUpdate}")
    private String databaseSchemaUpdate;
    @Value("${flowable.processEngineName}")
    private String processEngineName;

    //@Bean
    public ProcessEngine createProcessEngine() {
        ProcessEngineConfiguration configuration =
                ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        configuration.setJdbcDriver(jdbcDriver);
        configuration.setJdbcPassword(jdbcPassword);
        configuration.setJdbcUrl(jdbcUrl);
        configuration.setJdbcUsername(jdbcUsername);
        configuration.setDatabaseSchemaUpdate(databaseSchemaUpdate);
        configuration.setEngineName(processEngineName);
        ProcessEngine engine = configuration.buildProcessEngine();
        System.out.println(engine.getName());
        return engine;
    }

    //@Bean
    public ProcessEngineConfiguration createProcessEngineConfiguration() {
        ProcessEngineConfiguration configuration =
                ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        configuration.setJdbcDriver("org.h2.Driver");
        configuration.setJdbcPassword("");
        configuration.setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=1000");
        configuration.setJdbcUsername("sa");
        configuration.setDatabaseSchemaUpdate("true");
        configuration.setEngineName("code-config-engine");
        return configuration;
    }

}
