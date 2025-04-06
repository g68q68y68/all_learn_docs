package com.bpm.example.demo1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ExecutionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class RunGatewayDynamicJumpDemo extends FlowableEngineUtil {
    SimplePropertyPreFilter taskFilter = new SimplePropertyPreFilter(Task.class, "id",
            "name", "executionId", "taskDefinitionKey", "assignee");
    SimplePropertyPreFilter executionFilter = new SimplePropertyPreFilter(Execution.class,
            "id", "parentId", "processInstanceId", "activityId");

    @Test
    public void runGatewayDynamicJumpDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/GatewayDynamicJumpProcess.bpmn20.xml");

        //初始化流程变量
        Map<String, Object> varMap = ImmutableMap.of("condition1", true,
                "condition2", true, "condition3", true,
                "condition4", false, "condition5", true);
        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("gatewayDynamicJumpProcess", varMap);
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        ExecutionQuery executionQuery = runtimeService.createExecutionQuery()
                .processInstanceId(procInst.getId());
        //查询并办理第一个用户任务
        Task task1 = taskQuery.singleResult();
        taskService.complete(task1.getId());
        log.info("第一次跳转前，当前任务为：{}", JSON.toJSONString(taskQuery.list(), taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //从UserTask2跳转到UserTask6、UserTask7
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(procInst.getId())
                .moveSingleActivityIdToActivityIds("task2",
                        Arrays.asList("task6", "task7"))
                .changeState();
        log.info("第一次跳转后，当前任务为：{}", JSON.toJSONString(taskQuery.list(), taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //从UserTask3跳转到UserTask10
        Task task3 = taskQuery.list().stream()
                .filter(t -> "task3".equals(t.getTaskDefinitionKey()))
                .findFirst().get();
        runtimeService.createChangeActivityStateBuilder()
                .moveExecutionToActivityId(task3.getExecutionId(), "task10")
                .changeState();
        log.info("第二次跳转后，当前任务为：{}", JSON.toJSONString(taskQuery.list(), taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //从UserTask4、UserTask10跳转到UserTask12
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(procInst.getId())
                .moveActivityIdsToSingleActivityId(Arrays.asList("task4", "task10"),
                        "task12")
                .changeState();
        log.info("第三次跳转后，当前任务为：{}", JSON.toJSONString(taskQuery.list(), taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //从UserTask12跳转到UserTask3、UserTask4
        Task task12 = taskQuery.list().stream()
                .filter(t -> "task12".equals(t.getTaskDefinitionKey()))
                .findFirst().get();
        runtimeService.createChangeActivityStateBuilder()
                .moveSingleExecutionToActivityIds(task12.getExecutionId(),
                        Arrays.asList("task3", "task4"))
                .changeState();
        log.info("第四次跳转后，当前任务为：{}", JSON.toJSONString(taskQuery.list(), taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //从UserTask3、UserTask4跳转到网关InclusiveGateway2
        List<Task> taskList1 = taskQuery.list().stream()
                .filter(t -> t.getTaskDefinitionKey().equals("task3")
                        || t.getTaskDefinitionKey().equals("task4"))
                .collect(Collectors.toList());
        List<String> executionList1 = taskList1.stream().map(Task::getExecutionId)
                .collect(Collectors.toList());
        runtimeService.createChangeActivityStateBuilder()
                .moveExecutionsToSingleActivityId(executionList1, "inclusiveGateway2")
                .changeState();
        log.info("第五次跳转后，当前任务为：{}", JSON.toJSONString(taskQuery.list(), taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //查询并办理UserTask6、UserTask7的任务
        List<Task> taskList2 = taskQuery.list().stream()
                .filter(t -> t.getTaskDefinitionKey().equals("task6")
                        || t.getTaskDefinitionKey().equals("task7"))
                .collect(Collectors.toList());
        taskList2.forEach(task -> {
            taskService.complete(task.getId());
        });
        //查询并办理UserTask11、UserTask12的任务
        List<Task> taskList3 = taskQuery.list().stream()
                .filter(t -> t.getTaskDefinitionKey().equals("task11")
                        || t.getTaskDefinitionKey().equals("task12"))
                .collect(Collectors.toList());
        taskList3.forEach(task -> {
            taskService.complete(task.getId());
        });

        //从UserTask13跳转到UserTask1
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(procInst.getId())
                .moveActivityIdTo("task13", "task1")
                .changeState();
        log.info("第六次跳转后，当前任务为：{}", JSON.toJSONString(taskQuery.list(), taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));
    }
}