package com.bpm.example.demo3;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ExecutionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class RunMultiSubprocessProcessDemo extends FlowableEngineUtil {
    SimplePropertyPreFilter executionFilter = new SimplePropertyPreFilter(Execution.class,
            "id", "parentId", "processInstanceId", "scope", "activityId");

    //初始化秘书信息
    private Map<String, String> secretaryMap = ImmutableMap.of("hebo", "huhaiqin",
            "liuxiaopeng", "litao", "wangjunlin", "liushaoli");

    @Test
    public void runMultiSubprocessProcessDemo()  {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/MultiSubprocessProcess.bpmn20.xml");

        //发起流程
        ProcessInstance mainProcInst = runtimeService
                .startProcessInstanceByKey("multiSubprocessProcess");
        ExecutionQuery executionQuery = runtimeService.createExecutionQuery()
                .processInstanceId(mainProcInst.getId());
        //查询执行实例
        List<Execution> executionList1 = executionQuery.list();
        log.info("主流程发起后，执行实例数为：{}，分别为：{}", executionList1.size(),
                JSON.toJSONString(executionList1, executionFilter));
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(mainProcInst.getId());
        //查询主流程第一个任务
        Task firstTaskOfMainProcess = taskQuery.singleResult();
        log.info("主流程当前所在节点为：{}", firstTaskOfMainProcess.getName());
        //设置主流程的流程变量
        List<String> assigneeList = Arrays.asList("wangjunlin", "liuxiaopeng", "hebo");
        Map<String, Object> varMap1 = ImmutableMap.of("assigneeList", assigneeList);
        //完成主流程第一个任务，启动子流程
        taskService.complete(firstTaskOfMainProcess.getId(), varMap1);
        //查询执行实例
        List<Execution> executionList2 = executionQuery.list();
        log.info("子流程发起后，执行实例数为：{}，分别为：{}", executionList2.size(),
                JSON.toJSONString(executionList2, executionFilter));

        //查询子流程第一个节点的任务，并依次完成
        List<Task> firstTasksOfSubProcess = taskQuery.list();
        for (Task firstTaskOfSubProcess : firstTasksOfSubProcess) {
            log.info("子流程当前所在节点为：{}，taskId：{}，办理人：{}",
                    firstTaskOfSubProcess.getName(), firstTaskOfSubProcess.getId(),
                    firstTaskOfSubProcess.getAssignee());
            Map<String, Object> varMap2 = ImmutableMap.of("nextUserId",
                    secretaryMap.get(firstTaskOfSubProcess.getAssignee()));
            taskService.complete(firstTaskOfSubProcess.getId(), varMap2);
        }

        //查询子流程第二个节点的任务，并依次完成
        List<Task> secondTasksOfSubProcess = taskQuery.list();
        for (Task secondTaskOfSubProcess : secondTasksOfSubProcess) {
            log.info("子流程当前所在节点为：{}，taskId：{}，办理人：{}",
                    secondTaskOfSubProcess.getName(), secondTaskOfSubProcess.getId(),
                    secondTaskOfSubProcess.getAssignee());
            taskService.complete(secondTaskOfSubProcess.getId());
        }

        //查询执行实例
        List<Execution> executionList3 = executionQuery.list();
        log.info("子流程结束后，执行实例数为：{}，执行实例信息为：{}", executionList3.size(),
                JSON.toJSONString(executionList3, executionFilter));

        //查询主流程第二个任务
        Task secondTaskOfMainProcess = taskQuery.singleResult();
        log.info("主流程所在当前节点为：" + secondTaskOfMainProcess.getName());
        taskService.complete(secondTaskOfMainProcess.getId());

        //查询执行实例
        List<Execution> executionList4 = executionQuery.list();
        log.info("主流程结束后，执行实例数为：{}，执行实例信息为：{}", executionList4.size(),
                JSON.toJSONString(executionList4, executionFilter));
    }
}