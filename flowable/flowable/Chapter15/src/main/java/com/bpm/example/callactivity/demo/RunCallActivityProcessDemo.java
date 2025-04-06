package com.bpm.example.callactivity.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ExecutionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class RunCallActivityProcessDemo extends FlowableEngineUtil {
    SimplePropertyPreFilter executionFilter = new SimplePropertyPreFilter(Execution.class,
            "id", "name", "businessKey", "processInstanceId", "rootProcessInstanceId",
            "superExecutionId", "scope", "parentId", "activityId", "processDefinitionKey");

    @Test
    public void runCallActivityProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署父、子流程
        deployResource("processes/ApproveLoanSubProcess.bpmn20.xml");
        deployResource("processes/ApproveLoanMainProcess.bpmn20.xml");

        //设置主流程的流程变量
        Map<String, Object> varMap1 = ImmutableMap.of("lender", "huhaiqin",
                "subProcessBusinessKey", UUID.randomUUID().toString(),
                "subProcessBusinessName", "胡海琴的贷款申请");
        //发起父流程
        ProcessInstance mainProcessInstance = runtimeService
                .startProcessInstanceByKey("approveLoanMainProcess", varMap1);
        ExecutionQuery executionQuery = runtimeService.createExecutionQuery()
                .rootProcessInstanceId(mainProcessInstance.getId());
        //根据根流程实例编号查询执行实例
        List<Execution> executionList1 = executionQuery.list();
        log.info("父流程发起后，执行实例总数为{}，分别为：{}", executionList1.size(),
                JSON.toJSONString(executionList1, executionFilter));
        //查询父流程的流程变量
        Map<String, Object> mainProcessVarMap1 = runtimeService
                .getVariables(mainProcessInstance.getId());
        log.info("父流程的流程变量为：{}", mainProcessVarMap1);
        //查询父流程第一个任务
        Task firstTaskOfMainProcess = taskService.createTaskQuery()
                .processInstanceId(mainProcessInstance.getId()).singleResult();
        log.info("父流程发起后，流程当前所在用户任务为：{}", firstTaskOfMainProcess.getName());
        //完成父流程第一个任务
        taskService.complete(firstTaskOfMainProcess.getId());
        log.info("完成用户任务：{}，启动子流程", firstTaskOfMainProcess.getName());
        //查询执行实例
        List<Execution> executionList2 = executionQuery.list();
        log.info("子流程发起后，执行实例总数为{}，分别为：{}", executionList2.size(),
                JSON.toJSONString(executionList2, executionFilter));
        //查询子流程的流程实例编号（通过callActivity的flowable:idVariableName属性指定）
        String subProcessInstanceId = (String) runtimeService
                .getVariable(mainProcessInstance.getId(), "subProcessInstanceId");
        //查询子流程第一个任务
        Task firstTaskOfSubProcess = taskService.createTaskQuery()
                .processInstanceId(subProcessInstanceId).singleResult();
        log.info("子流程发起后，子流程当前所在用户任务为：{}", firstTaskOfSubProcess.getName());
        //查询子流程的流程变量
        Map<String, Object> subProcessVarMap1 = runtimeService
                .getVariables(subProcessInstanceId);
        log.info("子流程的流程变量为：{}", subProcessVarMap1);
        //设置子流程的流程变量
        Map<String, Object> varMap2 = ImmutableMap.of("loanAmount", 100000);
        //完成子流程第一个任务，子流程结束
        taskService.complete(firstTaskOfSubProcess.getId(), varMap2);
        log.info("完成用户任务：{}，结束子流程", firstTaskOfSubProcess.getName());
        //查询执行实例
        List<Execution> executionList3 = executionQuery.list();
        log.info("子流程结束后，回到主流程，执行实例总数为{}，分别为：{}", executionList3.size(),
                JSON.toJSONString(executionList3, executionFilter));
        Map<String, Object> mainProcessVarMap2 = runtimeService
                .getVariables(mainProcessInstance.getId());
        log.info("父流程的流程变量为：{}", mainProcessVarMap2);
        //查询父流程第二个任务
        Task secondTaskOfMainProcess = taskService.createTaskQuery()
                .processInstanceId(mainProcessInstance.getId()).singleResult();
        log.info("流程当前所在用户任务为：" + secondTaskOfMainProcess.getName());
        //完成父流程第二个任务，主流程结束
        taskService.complete(secondTaskOfMainProcess.getId());
        log.info("完成用户任务：{}，结束父流程", secondTaskOfMainProcess.getName());
        //查询执行实例
        List<Execution> executionList4 = executionQuery.list();
        log.info("主流程结束后，执行实例总数为{}，分别为：{}", executionList4.size(),
                JSON.toJSONString(executionList4, executionFilter));
    }
}
