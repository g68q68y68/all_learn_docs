package com.bpm.example.subprocess.demo2;

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
public class RunEventSubProcessInMainProcessDemo extends FlowableEngineUtil {
    SimplePropertyPreFilter executionFilter = new SimplePropertyPreFilter(Execution.class,
            "id", "parentId", "scope", "businessKey", "processInstanceId",
            "rootProcessInstanceId", "activityId");

    @Test
    public void runEventSubProcessInMainProcessDemo()  {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/EventSubProcessInMainProcess.bpmn20.xml");

        //发起流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("eventSubProcessInMainProcess");
        ExecutionQuery executionQuery = runtimeService.createExecutionQuery()
                .processInstanceId(procInst.getId());
        //查询执行实例
        List<Execution> executionList1 = executionQuery.list();
        log.info("主流程发起后，执行实例数为{}，分别为：{}", executionList1.size(),
                JSON.toJSONString(executionList1, executionFilter));
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询主流程第一个任务
        Task firstTaskOfMainProcess = taskQuery.singleResult();
        log.info("主流程发起后，流程当前所在的用户任务为：{}",
                firstTaskOfMainProcess.getName());
        //完成主流程第一个任务
        taskService.complete(firstTaskOfMainProcess.getId());
        log.info("完成用户任务：{}", firstTaskOfMainProcess.getName());
        //查询主流程第二个任务
        Task secondTaskOfMainProcess = taskQuery.singleResult();
        log.info("完成用户任务{}后，流程当前所在的用户任务为：{}",
                firstTaskOfMainProcess.getName(), secondTaskOfMainProcess.getName());
        //设置流程变量
        Map<String, Object> varMap = ImmutableMap.of("operateResult", false);
        //完成主流程第二个任务，流程结束，抛出错误信号
        taskService.complete(secondTaskOfMainProcess.getId(), varMap);
        log.info("完成用户任务：{}，发起子流程", secondTaskOfMainProcess.getName());

        //查询执行实例
        List<Execution> executionList2 = executionQuery.list();
        log.info("子流程发起后，执行实例数为{}，分别为：{}", executionList2.size(),
                JSON.toJSONString(executionList2, executionFilter));

        //查询子流程第一个任务
        Task firstTaskOfSubProcess = taskQuery.singleResult();
        log.info("子流程发起后，流程当前所在的用户任务为：{}", firstTaskOfSubProcess.getName());
        //完成子流程第一个任务，子流程结束
        taskService.complete(firstTaskOfSubProcess.getId());
        log.info("完成用户任务：{}，子流程结束", firstTaskOfSubProcess.getName());

        //查询执行实例
        List<Execution> executionList4 = executionQuery.list();
        log.info("主流程结束后，执行实例数为{}，执行实例信息为：{}", executionList4.size(),
                JSON.toJSONString(executionList4, executionFilter));
    }
}
