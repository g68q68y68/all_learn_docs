package com.bpm.example.demo6;

import com.bpm.example.demo6.deploy.MultiTenantDeployUtil;
import com.bpm.example.demo6.multidatasource.MultiTenantDataSourceProcessEngineConfiguration;
import com.bpm.example.demo6.multidatasource.MultiTenantInfoHolder;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:flowable.multidatasource.xml")
public class RunMultiTenantDemo {
    @Autowired
    private MultiTenantInfoHolder multiTenantInfoHolder;
    @Autowired
    private MultiTenantDeployUtil multiTenantDeployUtil;
    @Autowired
    private MultiTenantDataSourceProcessEngineConfiguration configuration;

    @Test
    public void runMultiTenantDemo() {
        //创建流程引擎并获取RepositoryService服务
        ProcessEngine processEngine = configuration
                .buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //在不同的租户下部署流程定义
        multiTenantDeployUtil.deployByTenant(repositoryService, "tenantId1",
                "customValidatorProcess", "processes/CustomValidatorProcess.bpmn20.xml");
        multiTenantDeployUtil.deployByTenant(repositoryService, "tenantId2",
                "customValidatorProcess", "processes/CustomValidatorProcess.bpmn20.xml");
        //查询不同租户下的流程定义信息
        multiTenantDeployUtil.queryProcessDefinitionFromTenant(repositoryService,
                "tenantId1", "customValidatorProcess");
        multiTenantDeployUtil.queryProcessDefinitionFromTenant(repositoryService,
                "tenantId2", "customValidatorProcess");
    }
}
