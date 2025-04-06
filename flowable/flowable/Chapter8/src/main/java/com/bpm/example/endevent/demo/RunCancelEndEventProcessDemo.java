package com.bpm.example.endevent.demo;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElementsContainer;
import org.flowable.bpmn.model.SubProcess;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

@Slf4j
public class RunCancelEndEventProcessDemo extends FlowableEngineUtil {
    @Test
    public void runCancelEndEventProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/CancelEndEventProcess.bpmn20.xml");

        //发起流程
        ProcessInstance mainProcessInstance = runtimeService
                .startProcessInstanceByKey("cancelEndEventProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(mainProcessInstance.getId());
        //查询子流程第一个任务
        Task firstTaskOfSubProcess = taskQuery.singleResult();
        log.info("发起流程后，当前流程所在用户任务为：{}", firstTaskOfSubProcess.getName());
        //查询BpmnModel
        BpmnModel bpmnModel = repositoryService
                .getBpmnModel(mainProcessInstance.getProcessDefinitionId());
        //查询当前用户任务所在的父容器
        FlowElementsContainer firstTaskOfSubProcessContainer = bpmnModel
                .getFlowElement(firstTaskOfSubProcess.getTaskDefinitionKey())
                .getParentContainer();
        log.info("用户任务{}处于{}中", firstTaskOfSubProcess.getName(),
                firstTaskOfSubProcessContainer instanceof SubProcess ? "子流程" : "主流程");
        //完成子流程第一个任务
        taskService.complete(firstTaskOfSubProcess.getId());
        //查询主流程第一个任务
        Task firstTaskOfMainProcess = taskQuery.singleResult();
        log.info("完成第一个用户任务后，当前流程所在用户任务为：{}", firstTaskOfMainProcess.getName());
        //查询当前用户任务所在的父容器
        FlowElementsContainer firstTaskOfMainProcessContainer = bpmnModel
                .getFlowElement(firstTaskOfMainProcess.getTaskDefinitionKey())
                .getParentContainer();
        log.info("用户任务{}处于{}中", firstTaskOfMainProcess.getName(),
                firstTaskOfMainProcessContainer instanceof SubProcess ? "子流程" : "主流程");
        //完成子流程第一个任务
        taskService.complete(firstTaskOfMainProcess.getId());
    }
}
