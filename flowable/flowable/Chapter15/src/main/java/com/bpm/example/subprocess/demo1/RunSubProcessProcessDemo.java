package com.bpm.example.subprocess.demo1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ExecutionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.job.api.Job;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.List;
import java.util.Map;

@Slf4j
public class RunSubProcessProcessDemo extends FlowableEngineUtil {
    SimplePropertyPreFilter executionFilter = new SimplePropertyPreFilter(Execution.class,
            "id", "parentId", "processInstanceId", "processDefinitionId",
            "rootProcessInstanceId", "scope", "activityId", "businessKey");
    SimplePropertyPreFilter jobFilter = new SimplePropertyPreFilter(Job.class,
            "id", "executionId", "duedate", "jobHandlerType", "jobType",
            "processDefinitionId", "processInstanceId", "retries");

    @Test
    public void runSubProcessProcessDemo() throws Exception {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.job.xml");
        //部署流程
        deployResource("processes/SubProcessProcess.bpmn20.xml");

        //设置主流程的流程变量
        Map<String, Object> varMap1 = ImmutableMap.of("lender", "huhaiqin");
        //发起流程
        ProcessInstance mainProcessInstance = runtimeService
                .startProcessInstanceByKey("subProcessProcess", varMap1);
        ExecutionQuery executionQuery = runtimeService.createExecutionQuery()
                .processInstanceId(mainProcessInstance.getId());
        //查询执行实例
        List<Execution> executionList1 = executionQuery.list();
        log.info("主流程发起后，执行实例数为{}，分别为：{}", executionList1.size(),
                JSON.toJSONString(executionList1, executionFilter));
        //查询主流程的流程变量
        Map<String, Object> mainProcessVarMap1 = runtimeService
                .getVariables(mainProcessInstance.getId());
        log.info("主流程的流程变量为：{}", mainProcessVarMap1);
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(mainProcessInstance.getId());
        //查询主流程第一个任务
        Task firstTaskOfMainProcess = taskQuery.singleResult();
        log.info("主流程发起后，流程当前所在用户任务为：{}", firstTaskOfMainProcess.getName());
        //完成主流程第一个任务
        taskService.complete(firstTaskOfMainProcess.getId());
        log.info("完成用户任务：{}，启动子流程", firstTaskOfMainProcess.getName());
        //查询执行实例
        List<Execution> executionList2 = executionQuery.list();
        log.info("子流程发起后，执行实例数为{}，分别为：{}", executionList2.size(),
                JSON.toJSONString(executionList2, executionFilter));
        //查询子流程第一个任务
        Task firstTaskOfSubProcess = taskQuery.singleResult();
        log.info("子流程发起后，流程当前所在用户任务为：{}", firstTaskOfSubProcess.getName());
        //查询子流程的流程变量
        Map<String, Object> subProcessVarMap1 = runtimeService
                .getVariables(firstTaskOfSubProcess.getProcessInstanceId());
        log.info("子流程的流程变量为：{}", subProcessVarMap1);
        //设置子流程的流程变量
        Map<String, Object> varMap2 = ImmutableMap.of("loanAmount", 10000000);
        //完成子流程第一个任务
        taskService.complete(firstTaskOfSubProcess.getId(), varMap2);
        log.info("完成用户任务：{}", firstTaskOfSubProcess.getName());
        //查询子流程第一个任务
        Task secondTaskOfSubProcess = taskQuery.singleResult();
        log.info("完成用户任务：{}后，流程当前所在用户任务为：{}",
                firstTaskOfSubProcess.getName(), secondTaskOfSubProcess.getName());
        Map<String, Object> mainProcessVarMap2 = runtimeService
                .getVariables(mainProcessInstance.getId());
        log.info("主流程的流程变量为：{}", mainProcessVarMap2);

        //查询定时任务
        List<Job> jobs = managementService.createTimerJobQuery().list();
        log.info("定时任务有：{}", JSON.toJSONString(jobs, jobFilter));
        log.info("暂停90秒...");
        //暂停90秒,触发边界定时事件，流程结束
        Thread.sleep(90 * 1000);

        //查询执行实例
        List<Execution> executionList4 = executionQuery.list();
        log.info("主流程结束后，执行实例数为{}，执行实例信息为：{}", executionList4.size(),
                JSON.toJSONString(executionList4, executionFilter));
    }
}