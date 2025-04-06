package com.bpm.example.sequenceflow.demo;

import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.Map;

@Slf4j
public class RunConditionalSequenceFlowProcessDemo extends FlowableEngineUtil {
    @Test
    public void runConditionalSequenceFlowProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/ConditionalSequenceFlowProcess.bpmn20.xml");

        //启动流程
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("conditionalSequenceFlowProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId());
        //查询采购申请任务
        Task userApplyTask = taskQuery.singleResult();
        //设置totalPrice变量值
        Map<String, Object> varMap = ImmutableMap.of("totalPrice", 15000);
        //完成采购申请任务
        taskService.complete(userApplyTask.getId(), varMap);
        //查询审批任务
        Task approveTask = taskQuery.singleResult();
        log.info("审批任务taskId：{}，节点名称：{}", approveTask.getId(), approveTask.getName());
        //完成审批任务
        taskService.complete(approveTask.getId());
    }
}