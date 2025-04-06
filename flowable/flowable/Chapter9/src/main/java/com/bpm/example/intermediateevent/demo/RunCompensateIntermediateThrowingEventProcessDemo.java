package com.bpm.example.intermediateevent.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ExecutionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.List;
import java.util.Map;

@Slf4j
public class RunCompensateIntermediateThrowingEventProcessDemo
        extends FlowableEngineUtil {
    SimplePropertyPreFilter executionFilter = new SimplePropertyPreFilter(Execution.class,
            "id", "parentId", "businessKey", "processInstanceId", "superExecutionId",
            "rootProcessInstanceId", "scope", "activityId");

    @Test
    public void runCompensateIntermediateThrowingEventProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition procDef = deployResource("processes" +
                "/CompensateIntermediateThrowingEventProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceById(procDef.getId());
        ExecutionQuery executionQuery = runtimeService.createExecutionQuery()
                .processInstanceId(procInst.getId());
        //查询执行实例
        List<Execution> executionList1 = executionQuery.list();
        log.info("主流程发起后，执行实例数为：{}，分别为：{}", executionList1.size(),
                JSON.toJSONString(executionList1, executionFilter));
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询“预报名”任务
        Task firstTask = taskQuery.taskName("预报名").singleResult();
        //设置流程变量
        Map<String, Object> varMap1 = ImmutableMap.of("applicant", "zhangsan");
        //完成“预报名”任务
        taskService.complete(firstTask.getId(), varMap1);
        log.info("办理完成名称为：{}的用户任务", firstTask.getName());
        //查询执行实例
        List<Execution> executionList2 = executionQuery.list();
        log.info("子流程发起后，执行实例数为：{}，分别为：{}", executionList2.size(),
                JSON.toJSONString(executionList2, executionFilter));
        //查询“正式报名”任务
        Task secondTask = taskQuery.taskName("正式报名").singleResult();
        //完成“正式报名”任务
        taskService.complete(secondTask.getId());
        log.info("办理完成名称为：{}的用户任务", secondTask.getName());
        //查询“报名审核”任务
        Task thirdTask = taskQuery.taskName("报名审核").singleResult();
        //完成“报名审核”任务
        taskService.complete(thirdTask.getId());
        log.info("办理完成名称为：{}的用户任务", thirdTask.getName());
        //查询执行实例
        List<Execution> executionList3 = executionQuery.list();
        log.info("子流程结束后，执行实例数为：{}，分别为：{}", executionList3.size(),
                JSON.toJSONString(executionList3, executionFilter));
        //查询“银行卡支付”任务
        Task fourthTask = taskQuery.taskName("银行卡支付").singleResult();
        log.info("即将办理名称为：{}的用户任务", fourthTask.getName());
        //设置流程变量
        Map<String, Object> varMap2 = ImmutableMap.of("applicationFee", 1000);
        //完成第二个任务（流程结束）
        taskService.complete(fourthTask.getId(), varMap2);
        //查询执行实例
        List<Execution> executionList4 = executionQuery.list();
        log.info("流程结束后，执行实例数为：{}，执行实例信息为：{}", executionList4.size(),
                JSON.toJSONString(executionList4, executionFilter));
    }
}