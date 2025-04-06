package com.bpm.example.demo1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ExecutionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class RunSubProcessDynamicJumpDemo extends FlowableEngineUtil {
    SimplePropertyPreFilter taskFilter = new SimplePropertyPreFilter(Task.class, "id",
            "name", "executionId", "taskDefinitionKey", "assignee");
    SimplePropertyPreFilter executionFilter = new SimplePropertyPreFilter(Execution.class,
            "id", "parentId", "processInstanceId", "activityId");

    @Test
    public void runSubProcessDynamicJumpDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/SubProcessDynamicJumpProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("subProcessDynamicJumpProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        ExecutionQuery executionQuery = runtimeService.createExecutionQuery()
                .processInstanceId(procInst.getId());

        Task task1 = taskQuery.singleResult();
        log.info("第一次跳转前，当前任务为：{}", JSON.toJSONString(task1, taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //在事件子流程EventSubProcess1内部从StartEvent4跳转到UserTask8、UserTask9
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(procInst.getId())
                .moveSingleActivityIdToActivityIds("startEvent4",
                        Arrays.asList("task8", "task9"))
                .changeState();
        log.info("第一次跳转后，当前任务为：{}", JSON.toJSONString(taskQuery.list(), taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //在事件子流程EventSubProcess1内部UserTask8、UserTask9跳转到UserTask10
        List<Task> taskList1 = taskQuery.list().stream()
                .filter(t -> t.getTaskDefinitionKey().equals("task8")
                        || t.getTaskDefinitionKey().equals("task9"))
                .collect(Collectors.toList());
        List<String> executionList1 = taskList1.stream()
                .map(Task::getExecutionId).collect(Collectors.toList());
        runtimeService.createChangeActivityStateBuilder()
                .moveExecutionsToSingleActivityId(executionList1, "task10")
                .changeState();
        log.info("第二跳转后，当前任务为：{}", JSON.toJSONString(taskQuery.list(), taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //从事件子流程EventSubProcess1的UserTask10跳转到子流程SubProcess1的UserTask3、SubProcess2的UserTask6
        Task task10 = taskQuery.list().stream()
                .filter(t -> t.getTaskDefinitionKey().equals("task10"))
                .findFirst().get();
        runtimeService.createChangeActivityStateBuilder()
                .moveSingleExecutionToActivityIds(task10.getExecutionId(),
                        Arrays.asList("task3", "task6"))
                .changeState();
        log.info("第三次跳转后，当前任务为：{}", JSON.toJSONString(taskQuery.list(), taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //从子流程SubProcess1的UserTask3、SubProcess2的UserTask6跳转到主流程的ParallelGateway2
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(procInst.getId())
                .moveActivityIdsToSingleActivityId(Arrays.asList("task3", "task6"),
                        "parallelGateway2")
                .changeState();
        log.info("第四次跳转后，当前任务为：{}", JSON.toJSONString(taskQuery.list(), taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //完成任务task2
        Task task2 = taskQuery.list().stream()
                .filter(t -> t.getTaskDefinitionKey().equals("task2"))
                .findFirst().get();
        taskService.complete(task2.getId());

        //从主流程的UserTask1跳转到主流程的UserTask2
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(procInst.getId())
                .moveActivityIdTo("task1", "task2")
                .changeState();
        log.info("第五次跳转后，当前任务为：{}", JSON.toJSONString(taskQuery.list(), taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));
    }
}