package com.bpm.example;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;

public class RunDemo1 {
    public static void main(String[] args) {
        //创建工作流引擎配置
        ProcessEngineConfigurationImpl processEngineConfiguration
                = (ProcessEngineConfigurationImpl) ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("flowable.cfg.xml");
        //创建工作流引擎
        ProcessEngine engine = processEngineConfiguration.buildProcessEngine();
        //获取流程存储服务
        RepositoryService repositoryService = engine.getRepositoryService();
        //部署流程
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("processes/SimpleProcess.bpmn20.xml")
                .deploy();
        //查询流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        System.out.println("流程定义ID为：" + processDefinition.getId() + "，流程名称为：" +
                processDefinition.getName() + "，版本号：" + processDefinition.getVersion());
        //再次部署流程
        Deployment deployment2 = repositoryService.createDeployment()
                .addClasspathResource("processes/SimpleProcess.bpmn20.xml")
                .deploy();
        //再次查询流程定义
        ProcessDefinition processDefinition2 = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment2.getId()).singleResult();
        System.out.println("流程定义ID为：" + processDefinition2.getId() + "，流程名称为：" +
                processDefinition2.getName() + "，版本号：" + processDefinition2.getVersion());
    }
}

