package com.demo.example.demo2;

import com.demo.example.common.util.FlowableEngineUtil;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class DeployQueryDemo extends FlowableEngineUtil {
    @Before
    public void preMethod() {
        //初始化流程引擎
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
    }

    @After
    public void postMethod() {
        //关闭流程引擎
        closeEngine();
    }

    @Test
    public void deploy() {
        //创建DeploymentBuilder
        repositoryService.createDeployment()
                //设置部署基本属性
                .key("HolidayRequest")
                .name("请假申请")
                .category("HR")
                .tenantId("HR")
                //添加classpath下的流程定义资源
                .addClasspathResource("processes/HolidayRequest.bpmn20.xml")
                //执行部署
                .deploy();
        //部署记录查询
        Deployment deployment = repositoryService.createDeploymentQuery()
                .deploymentKey("HolidayRequest")
                .singleResult();
        System.out.println("deployment_id = " + deployment.getId());
    }

    /**
     * 根据部署key查找最新的部署记录
     *
     * @param deploymentKey 部署key
     * @return 单条部署记录
     */
    public Deployment queryLastDeploymentByKey(String deploymentKey) {
        return repositoryService.createDeploymentQuery()
                //指定部署key
                .deploymentKey(deploymentKey)
                //查找最新版本
                .latest()
                //返回单个记录
                .singleResult();
    }


    /**
     * 根据部署key查找部署记录列表
     *
     * @param deploymentKey 部署key
     * @return 部署记录列表
     */
    public List<Deployment> queryDeploymentsByKey(String deploymentKey) {
        return repositoryService.createDeploymentQuery()
                //指定流程定义key
                .deploymentKey(deploymentKey)
                //按部署时间排序
                .orderByDeploymentTime()
                //降序
                .desc()
                //返回全部记录
                .list();
    }

    /**
     * 根据流程key查找最新的流程定义
     *
     * @param processDefinitionKey 流程定义key
     * @return 单条流程定义
     */
    public ProcessDefinition queryLastProcessDefinitionByKey(String processDefinitionKey) {
        return repositoryService.createProcessDefinitionQuery()
                //指定流程定义key
                .processDefinitionKey(processDefinitionKey)
                //指定激活状态
                .active()
                //查找最新版本
                .latestVersion()
                //返回单个记录
                .singleResult();
    }

    /**
     * 根据租户ID查找流程定义列表
     *
     * @param tenantId 租户ID
     * @return 流程定义列表
     */
    public List<ProcessDefinition> queryProcessDefinitionByTenantId(String tenantId) {
        return repositoryService.createProcessDefinitionQuery()
                //指定租户ID
                .processDefinitionTenantId(tenantId)
                //按流程key排序
                .orderByProcessDefinitionKey()
                //升序
                .asc()
                //返回全部记录
                .list();
    }

}