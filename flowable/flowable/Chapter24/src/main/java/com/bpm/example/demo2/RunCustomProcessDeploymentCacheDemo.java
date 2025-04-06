package com.bpm.example.demo2;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.junit.Test;

@Slf4j
public class RunCustomProcessDeploymentCacheDemo extends FlowableEngineUtil {
    @Test
    public void runCustomProcessDeploymentCacheDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cache.xml");
        //部署流程
        ProcessDefinition processDefinition
                = deployResource("processes/RedisCacheProcess.bpmn20.xml");
        //查询流程定义
        ProcessDefinition cacheProcessDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(processDefinition.getId())
                .singleResult();
        log.info("流程定义key为：{}，流程定义编号为：{}", cacheProcessDefinition.getKey(),
                cacheProcessDefinition.getId());
    }
}