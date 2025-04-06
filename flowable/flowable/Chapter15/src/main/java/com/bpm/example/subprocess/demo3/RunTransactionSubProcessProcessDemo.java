package com.bpm.example.subprocess.demo3;

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

import java.util.List;
import java.util.Map;

@Slf4j
public class RunTransactionSubProcessProcessDemo extends FlowableEngineUtil {
    SimplePropertyPreFilter executionFilter = new SimplePropertyPreFilter(Execution.class,
            "id", "parentId", "scope", "businessKey", "processInstanceId",
            "rootProcessInstanceId", "activityId");

    @Test
    public void runTransactionSubProcessProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/TransactionSubProcessProcess.bpmn20.xml");

        //设置主流程的流程变量
        Map<String, Object> varMap1 = ImmutableMap.of("goodsNum", 100, "orderCost", 1000);
        //发起流程
        ProcessInstance mainProcessInstance = runtimeService
                .startProcessInstanceByKey("transactionSubProcessProcess", varMap1);
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
        log.info("办理用户任务：{}，启动事务子流程", firstTaskOfMainProcess.getName());
        taskService.complete(firstTaskOfMainProcess.getId());
        //查询执行实例
        List<Execution> executionList2 = executionQuery.list();
        log.info("子流程发起后，执行实例数为{}，分别为：{}", executionList2.size(),
                JSON.toJSONString(executionList2, executionFilter));
        //查询子流程第一个任务
        Task firstTaskOfSubProcess = taskService.createTaskQuery()
                .taskName("用户支付订单").processInstanceId(mainProcessInstance.getId())
                .includeProcessVariables().singleResult();
        log.info("子流程发起后，流程当前所在用户任务为：{}", firstTaskOfSubProcess.getName());
        //查询子流程的流程变量
        Map<String, Object> subProcessVarMap1 = firstTaskOfSubProcess
                .getProcessVariables();
        log.info("子流程的流程变量为：{}", subProcessVarMap1);
        log.info("办理用户任务：{}", firstTaskOfSubProcess.getName());
        //完成子流程第一个任务，后续到达服务节点
        taskService.complete(firstTaskOfSubProcess.getId());
        //查询执行实例
        List<Execution> executionList4 = executionQuery.list();
        log.info("主流程结束后，执行实例数为{}，执行实例信息为：{}", executionList4.size(),
                JSON.toJSONString(executionList4, executionFilter));
    }
}
