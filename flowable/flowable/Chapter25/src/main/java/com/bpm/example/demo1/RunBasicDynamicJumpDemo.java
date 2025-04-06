package com.bpm.example.demo1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.impl.runtime.ChangeActivityStateBuilderImpl;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ExecutionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.List;
import java.util.Map;

@Slf4j
public class RunBasicDynamicJumpDemo extends FlowableEngineUtil {
    SimplePropertyPreFilter taskFilter = new SimplePropertyPreFilter(Task.class, "id",
            "name", "executionId", "taskDefinitionKey", "assignee", "owner");
    SimplePropertyPreFilter executionFilter = new SimplePropertyPreFilter(Execution.class,
            "id", "parentId", "processInstanceId", "activityId", "isScope");

    @Test
    public void runBasicDynamicJumpDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/BasicDynamicJumpProcess.bpmn20.xml");

        //初始化流程变量
        List<String> assigneeList1 = Lists.newArrayList("wangjunlin", "huhaiqin");
        List<String> assigneeList2 = Lists.newArrayList("liuxiaopeng", "hebo");
        Map<String, Object> varMap = ImmutableMap.of("assigneeList1", assigneeList1,
                "assigneeList2", assigneeList2);
        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("dynamicJumpProcess", varMap);
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        ExecutionQuery executionQuery = runtimeService.createExecutionQuery()
                .processInstanceId(procInst.getId());
        //查询第一个用户任务
        Task firstTask = taskQuery.processInstanceId(procInst.getId()).singleResult();
        List<Execution> executionList = executionQuery.list();
        log.info("第一次跳转前，当前任务为：{}", JSON.toJSONString(firstTask, taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionList, executionFilter));

        //从第1个节点跳转到第4个节点
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(procInst.getId())
                .moveExecutionToActivityId(firstTask.getExecutionId(), "fourthNode")
                .changeState();
        log.info("第一次跳转后，当前任务为：{}", JSON.toJSONString(taskQuery.list(), taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //从第4个节点跳转到第2个节点
    /*
        //查询第4个节点的用户任务
        Task fourthTask = taskService.createTaskQuery()
                .processInstanceId(procInst.getId())
                .taskDefinitionKey("fourthNode").singleResult();
        //查询用户任务的执行实例
        Execution execution = runtimeService.createExecutionQuery()
                .executionId(fourthTask.getExecutionId()).singleResult();
        //查询多实例用户任务的根执行实例
        Execution multiInstanceRootExecution = runtimeService.createExecutionQuery()
                .executionId(execution.getParentId()).singleResult();
        //从第4个节点跳转到第2个节点
        runtimeService.createChangeActivityStateBuilder()
                .moveExecutionToActivityId(multiInstanceRootExecution.getId(), "secondNode")
                .changeState();
    */
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(procInst.getId())
                .moveActivityIdTo("fourthNode", "secondNode")
                .changeState();

        log.info("第二次跳转后，当前任务为：{}",
                JSON.toJSONString(taskQuery.list(), taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));

        //从第2个节点跳转到第3个节点
        ((ChangeActivityStateBuilderImpl) runtimeService
                .createChangeActivityStateBuilder())
                .moveActivityIdTo("secondNode", "thirdNode", "liuwenjun", "")
                .processInstanceId(procInst.getId())
                .changeState();
        log.info("第三次跳转后，当前任务为：{}", JSON.toJSONString(taskQuery.list(), taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));
    }
}