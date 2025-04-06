package com.demo.example.demo3;

import com.demo.example.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.repository.EngineResource;
import org.flowable.engine.impl.persistence.entity.DeploymentEntity;
import org.flowable.engine.impl.persistence.entity.ResourceEntity;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.junit.Test;

import java.util.Map;

@Slf4j
public class DeployQueryDemo extends FlowableEngineUtil {
    @Test
    public void deploy() {
        //加载flowable配置文件并初始化工作流引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        //部署流程
        Deployment deployment = repositoryService.createDeployment()
                //设置部署基本属性
                .key("HolidayRequest")
                .name("请假申请")
                .category("HR")
                .tenantId("HR")
                //添加classpath下的流程定义资源
                .addClasspathResource("processes/HolidayRequest.bpmn20.xml")
                //执行部署
                .deploy();
        log.info("部署记录：deployment_id={}, deployment_name={}",
                deployment.getId(), deployment.getName());
        //查询流程资源
        log.info("部署资源:");
        if (deployment instanceof DeploymentEntity) {
            DeploymentEntity entity = (DeploymentEntity) deployment;
            Map<String, EngineResource> resourceEntityMap = entity.getResources();
            for (Map.Entry<String, EngineResource> resourceEntity :
                    resourceEntityMap.entrySet()) {
                EngineResource entityValue = resourceEntity.getValue();
                log.info("resource_name={}, deployment_id={}", entityValue.getName(),
                        entityValue.getDeploymentId());
            }
        }
        //查询流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                //指定流程定义key
                .processDefinitionKey("HolidayRequest")
                //指定激活状态
                .active()
                //查找最新版本
                .latestVersion()
                //返回单个记录
                .singleResult();
        log.info("流程定义:processDefinition_id={}, processDefinition_key={}",
                processDefinition.getId(), processDefinition.getKey());
        //关闭工作流引擎
        closeEngine();
    }
}

