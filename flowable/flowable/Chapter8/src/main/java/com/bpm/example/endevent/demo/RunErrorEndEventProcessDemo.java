package com.bpm.example.endevent.demo;

import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.Map;

@Slf4j
public class RunErrorEndEventProcessDemo extends FlowableEngineUtil {
    @Test
    public void runErrorEndEventProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/ErrorEndEventProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("errorEndEventProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询并完成下单服务
        Task orderTask = taskQuery.singleResult();
        taskService.complete(orderTask.getId());
        //查询付款任务
        Task payTask = taskQuery.singleResult();
        //设置payResult变量值
        Map<String, Object> varMap = ImmutableMap.of("payResult", false);
        //完成付款任务
        taskService.complete(payTask.getId(), varMap);
        //查看到达的任务
        Task task = taskQuery.singleResult();
        log.info("当前流程到达的用户任务名称为:{}",task.getName());
    }
}