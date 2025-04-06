package com.bpm.example.gateway.demo1;

import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.Map;

@Slf4j
public class RunExclusiveGatewayProcessDemo extends FlowableEngineUtil {
    @Test
    public void runExclusiveGatewayProcessDemo() {
        //加载Flowalbe配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/ExclusiveGatewayProcess.bpmn20.xml");

        //设置variable1变量值
        Map<String, Object> varMap1= ImmutableMap.of("variable1", 1);
        //启动流程
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("exclusiveGatewayProcess", varMap1);
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId());
        //查询任务
        Task task1 = taskQuery.singleResult();
        log.info("流程发起后，第一个用户任务的名称：{}", task1.getName());
        //设置variable2变量值
        Map<String, Object> varMap2= ImmutableMap.of("variable2", 2);
        //完成任务
        taskService.complete(task1.getId(), varMap2);
        //查询任务
        Task task2 = taskQuery.singleResult();
        log.info("第一个任务提交后，流程当前所在的用户任务的名称：{}", task2.getName());
        //完成任务
        taskService.complete(task2.getId());
    }
}