package com.bpm.example.demo1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.impl.runtime.ChangeActivityStateBuilderImpl;
import org.flowable.engine.repository.ProcessDefinition;
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
public class RunCallActivityDynamicJumpDemo extends FlowableEngineUtil {
    SimplePropertyPreFilter taskFilter = new SimplePropertyPreFilter(Task.class, "id",
            "name", "executionId", "taskDefinitionKey", "assignee");
    SimplePropertyPreFilter executionFilter = new SimplePropertyPreFilter(Execution.class,
            "id", "parentId", "processInstanceId", "activityId");

    @Test
    public void runCallActivityDynamicJumpDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition parentProcessDefinition = deployResource
                ("processes/CallActivityDynamicJumpMainProcess.bpmn20.xml");
        ProcessDefinition subProcessDefinition = deployResource
                ("processes/CallActivityDynamicJumpSubProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceById(parentProcessDefinition.getId());
        ExecutionQuery executionQuery = runtimeService.createExecutionQuery()
                .rootProcessInstanceId(procInst.getId());

        Task task1 = getTaskQuery(executionQuery).singleResult();
        log.info("第一次跳转前，当前任务为：{}", JSON.toJSONString(task1, taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //从主流程的UserTask1跳转子流程的UserTask5
        getChangeActivityStateBuilder()
                .processInstanceId(procInst.getId())
                .moveActivityIdToSubProcessInstanceActivityId("task1",
                        "task5", "callActivity1")
                .changeState();
        log.info("第一次跳转后，当前任务为：{}", JSON.toJSONString(getTaskQuery(executionQuery).list(),
                taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //从子流程的UserTask5跳转主流程的UserTask4
        getChangeActivityStateBuilder()
                .processInstanceId(getSubProcessInstanceId(procInst.getId()))
                .moveActivityIdToParentActivityId("task5", "task4")
                .changeState();
        log.info("第二次跳转后，当前任务为：{}", JSON.toJSONString(getTaskQuery(executionQuery).list(),
                taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //从主流程的UserTask4跳转子流程的UserTask7、UserTask8
        getChangeActivityStateBuilder()
                .moveSingleActivityIdToSubProcessInstanceActivityIds("task4",
                        Arrays.asList("task7", "task8"), "callActivity1",
                        subProcessDefinition.getVersion())
                .processInstanceId(procInst.getId())
                .changeState();
        log.info("第三次跳转后，当前任务为：{}", JSON.toJSONString(getTaskQuery(executionQuery).list(),
                taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //从子流程的UserTask7、UserTask8跳转主流程的UserTask1
        getChangeActivityStateBuilder()
                .moveActivityIdsToParentActivityId(Arrays.asList("task7", "task8"),
                        "task1", "huhaiqin", "")
                .processInstanceId(getSubProcessInstanceId(procInst.getId()))
                .changeState();
        log.info("第四次跳转后，当前任务为：{}", JSON.toJSONString(getTaskQuery(executionQuery).list(),
                taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //从主流程的UserTask1跳转到主流程的UserTask2、UserTask3
        getChangeActivityStateBuilder()
                .processInstanceId(procInst.getId())
                .moveSingleActivityIdToActivityIds("task1", Arrays.asList("task2", "task3"))
                .changeState();
        log.info("第五次跳转后，当前任务为：{}", JSON.toJSONString(getTaskQuery(executionQuery).list(),
                taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //从主流程的UserTask2、UserTask3跳转到子流程的UserTask10
        getChangeActivityStateBuilder()
                .moveActivityIdsToSubProcessInstanceActivityId(Arrays.asList("task2", "task3"),
                        "task10", "callActivity1",
                        subProcessDefinition.getVersion(), "hebo", "")
                .processInstanceId(procInst.getId())
                .changeState();
        log.info("第六次跳转后，当前任务为：{}", JSON.toJSONString(getTaskQuery(executionQuery).list(),
                taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //从子流程的UserTask10流转到主流程的UserTask2、UserTask3
        getChangeActivityStateBuilder()
                .moveSingleActivityIdToParentActivityIds("task10",
                        Arrays.asList("task2", "task3"))
                .processInstanceId(getSubProcessInstanceId(procInst.getId()))
                .changeState();
        log.info("第七次跳转后，当前任务为：{}", JSON.toJSONString(getTaskQuery(executionQuery).list(),
                taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));
    }

    //获取查询主子流程中的所有用户任务的TaskQuery
    private TaskQuery getTaskQuery(ExecutionQuery executionQuery) {
        List<Execution> list = executionQuery.list();
        List<String> processIds = list.stream()
                .map(Execution::getProcessInstanceId)
                .distinct().collect(Collectors.toList());
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceIdIn(processIds);
        return taskQuery;
    }

    //获取调用活动所引用的流程实例编号
    private String getSubProcessInstanceId(String parentProcInstId) {
        ProcessInstance subProcInst = runtimeService
                .createProcessInstanceQuery()
                .superProcessInstanceId(parentProcInstId)
                .singleResult();
        return subProcInst.getId();
    }

    //获取ChangeActivityStateBuilder
    private ChangeActivityStateBuilderImpl getChangeActivityStateBuilder() {
        return ((ChangeActivityStateBuilderImpl) runtimeService
                .createChangeActivityStateBuilder());
    }
}