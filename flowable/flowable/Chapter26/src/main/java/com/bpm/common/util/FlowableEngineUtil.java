package com.bpm.common.util;

import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.flowable.engine.DynamicBpmnService;
import org.flowable.engine.FormService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.ProcessMigrationService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.ProcessDefinition;
import org.junit.After;

import java.io.File;
import java.io.InputStream;

@Data
public class FlowableEngineUtil {
    //流程引擎配置
    protected ProcessEngineConfigurationImpl processEngineConfiguration;
    //流程引擎
    protected ProcessEngine engine;
    //流程存储服务
    protected RepositoryService repositoryService;
    //运行时服务
    protected RuntimeService runtimeService;
    //任务服务
    protected TaskService taskService;
    //历史服务
    protected HistoryService historyService;
    //管理服务
    protected ManagementService managementService;
    //身份服务
    protected IdentityService identityService;
    //表单服务
    protected FormService formService;
    //动态Bpmn服务
    protected DynamicBpmnService dynamicBpmnService;
    //流程迁移服务
    protected ProcessMigrationService processMigrationService;

    /**
     * 初始化流程引擎及各种服务
     * @param resource Flowable配置文件
     */
    public void initFlowableEngineAndServices(String resource) {
        //创建流程引擎配置
        this.processEngineConfiguration
                = (ProcessEngineConfigurationImpl) ProcessEngineConfiguration.createProcessEngineConfigurationFromResource(resource);
        //创建流程引擎
        this.engine = processEngineConfiguration.buildProcessEngine();
        //获取流程存储服务
        this.repositoryService = engine.getRepositoryService();
        //获取运行时服务
        this.runtimeService = engine.getRuntimeService();
        //获取任务服务
        this.taskService = engine.getTaskService();
        //获取历史服务
        this.historyService = engine.getHistoryService();
        //获取管理服务
        this.managementService = engine.getManagementService();
        //获取身份服务
        this.identityService = engine.getIdentityService();
        //获取表单服务
        this.formService = engine.getFormService();
        //获取动态Bpmn服务
        this.dynamicBpmnService = engine.getDynamicBpmnService();
        //获取流程迁移服务
        this.processMigrationService = engine.getProcessMigrationService();
    }

    /**
     * 部署单个流程
     * @param resource  单个流程XML文件地址
     * @return
     */
    public ProcessDefinition deployResource(String resource) {
        //部署流程
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(resource).deploy();
        //查询流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        return processDefinition;
    }

    /**
     * 部署多个流程
     * @param resources  多个流程XML文件地址
     * @return
     */
    public ProcessDefinition deployResources(String... resources) {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        //部署流程
        for (String resource : resources) {
            deploymentBuilder.addClasspathResource(resource);
        }
        Deployment deployment = deploymentBuilder.deploy();
        //查询流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        return processDefinition;
    }

    /**
     * 导出流程
     * @param processDefinitionId  流程定义编号
     * @param photoName  图片名称
     * @throws Exception
     */
    public void exportProcessDiagram(String processDefinitionId, String photoName) throws Exception {
        InputStream processDiagram = repositoryService.getProcessDiagram(processDefinitionId);
        FileUtils.copyInputStreamToFile(processDiagram, new File(
                "d://" + photoName + ".png"));
    }

    /**
     * 导出流程图片
     * @param processDefinitionId  流程定义编号
     * @throws Exception
     */
    public void exportProcessDefinitionXml(String processDefinitionId) throws Exception{
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        InputStream processBpmn = repositoryService
                .getProcessModel(processDefinitionId);
        FileUtils.copyInputStreamToFile(processBpmn, new File(
                "d://" + processDefinition.getKey() + ".bpmn20.xml"));
    }

    /**
     * 关闭流程引擎
     */
    @After
    public void closeEngine() {
        engine.close();
    }
}
