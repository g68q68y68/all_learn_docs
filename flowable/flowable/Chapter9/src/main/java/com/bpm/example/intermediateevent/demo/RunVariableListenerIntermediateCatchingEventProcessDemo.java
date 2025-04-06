package com.bpm.example.intermediateevent.demo;

import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.Map;

@Slf4j
public class RunVariableListenerIntermediateCatchingEventProcessDemo
        extends FlowableEngineUtil {
    @Test
    public void runVariableListenerIntermediateCatchingEventProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition procDef = deployResource("processes" +
                "/VariableListenerIntermediateCatchingEventProcess.bpmn20.xml");

        //设置变量
        Map<String, Object> varMap1 = ImmutableMap.of("materialState", "init");
        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceById(procDef.getId(), varMap1);
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询并办理第一个任务
        Task firstTask = taskQuery.singleResult();
        taskService.complete(firstTask.getId());
        Task secondTask = taskQuery.singleResult();
        log.info("流程流转到：{}", secondTask.getName());
        //修改变量
        runtimeService.setVariable(procInst.getId(), "materialState", "fushen");
        Task thirdTask = taskQuery.singleResult();
        log.info("修改变量materialState值为fushen后，流程流转到：{}", thirdTask.getName());
        //完成第三个任务同时修改变量
        Map<String, Object> varMap2 = ImmutableMap.of("backgroundCheckState", "init");
        taskService.complete(thirdTask.getId(), varMap2);
        Task fourthTask = taskQuery.singleResult();
        log.info("新建变量backgroundCheckState后，流程流转到：{}", fourthTask.getName());
        //完成第四个任务同时修改变量
        Map<String, Object> varMap3 = ImmutableMap.of("materialState", "success");
        taskService.complete(fourthTask.getId(), varMap3);

        HistoricProcessInstance historicProcessInstance = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(procInst.getId()).singleResult();
        if (historicProcessInstance.getEndTime() != null) {
            log.info("修改变量materialState值为success后，流程已结束");
        }
    }
}