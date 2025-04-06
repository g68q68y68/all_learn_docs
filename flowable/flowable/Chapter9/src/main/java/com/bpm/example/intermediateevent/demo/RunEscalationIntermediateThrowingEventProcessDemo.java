package com.bpm.example.intermediateevent.demo;

import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class RunEscalationIntermediateThrowingEventProcessDemo
        extends FlowableEngineUtil {
    @Test
    public void runEscalationIntermediateThrowingEventProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition procDef = deployResource("processes" +
                "/EscalationIntermediateThrowingEventProcess.bpmn20.xml");

        ProcessInstance procInst = runtimeService
                .startProcessInstanceById(procDef.getId());
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询第一个任务
        Task task1 = taskQuery.singleResult();
        log.info("流程发起后，第一个任务名称为：{}", task1.getName());
        //设置流程变量并完成第一个任务
        Map<String, Object> varMap1 = ImmutableMap.of("isQualityIssue", true);
        taskService.complete(task1.getId(), varMap1);
        //查询流程当前任务
        List<Task> taskList1 = taskQuery.list();
        log.info("问题上报任务完成后，流程当前任务名称为：{}", taskList1.stream()
                .map(Task::getName).collect(Collectors.joining("，")));
        //设置流程变量，完成质量检查任务
        Map<String, Object> varMap2 = ImmutableMap.of("problemLeve", "serious");
        Task task2 = taskList1.stream().filter(task -> task.getName().equals("质量检查"))
                .findFirst().get();
        taskService.complete(task2.getId(), varMap2);
        //查询流程当前任务
        List<Task> taskList2 = taskQuery.list();
        log.info("质量检查任务完成后，流程当前任务名称为：{}", taskList2.stream()
                .map(Task::getName).collect(Collectors.joining("，")));
        for (Task task : taskList2) {
            log.info("办理任务：{}", task.getName());
            taskService.complete(task.getId());
        }
    }
}
