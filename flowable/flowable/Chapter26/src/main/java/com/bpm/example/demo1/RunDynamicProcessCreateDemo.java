package com.bpm.example.demo1;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo1.dynamic.DynamicProcessCreateUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Test;

import java.util.Map;

@Slf4j
public class RunDynamicProcessCreateDemo extends FlowableEngineUtil {
    @Test
    public void runDynamicProcessCreateDemo() throws Exception {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //初始化流程创建工具类
        DynamicProcessCreateUtil dynamicUtil = new DynamicProcessCreateUtil
                (repositoryService, new BpmnModel(), new Process());
        //创建流程
        dynamicUtil.createProcess("loanProcess", "借款流程");
        //创建开始节点
        StartEvent startEvent = dynamicUtil.createStartEvent("startEvent1", "开始节点");
        //创建"借款申请"用户任务
        UserTask userTask1 = dynamicUtil.createUserTask("userTask1", "借款申请", "employee");
        //创建开始节点到"借款申请"用户任务的顺序流
        dynamicUtil.createSequenceFlow(startEvent.getId(), userTask1.getId());
        //创建分支网关
        ExclusiveGateway exclusiveGateway = dynamicUtil.createExclusiveGateway
                ("exclusiveGateway1", "排他网关");
        //创建"借款申请"用户任务到分支网关的顺序流
        dynamicUtil.createSequenceFlow(userTask1.getId(), exclusiveGateway.getId());
        //创建"直属上级审批"用户任务
        UserTask userTask2 = dynamicUtil.createUserTask("userTask2", "直属上级审批", "leader");
        //创建分支网关到"直属上级审批"用户任务的顺序流
        dynamicUtil.createSequenceFlow(exclusiveGateway.getId(), userTask2.getId(),
                "${money<5000}");
        //创建"部门经理审批"用户任务
        UserTask userTask3 = dynamicUtil.createUserTask("userTask3", "部门经理审批", "manager");
        //创建分支网关到"部门经理审批"用户任务的顺序流
        dynamicUtil.createSequenceFlow(exclusiveGateway.getId(), userTask3.getId(),
                "${money>=5000}");
        //创建结束任务
        EndEvent endEvent = dynamicUtil.createEndEvent("endEvent1", "结束任务");
        //创建"直属上级审批"用户任务到结束任务的顺序流
        dynamicUtil.createSequenceFlow(userTask2.getId(), endEvent.getId());
        //创建"部门经理审批"用户任务到结束任务的顺序流
        dynamicUtil.createSequenceFlow(userTask3.getId(), endEvent.getId());
        //流程自动布局
        dynamicUtil.autoLayout();
        //部署流程
        ProcessDefinition procDef = dynamicUtil.deployProcess();
        //导出流程图
        exportProcessDiagram(procDef.getId(), procDef.getName());
        //导出流程BPMN2.0 XML文件
        exportProcessDefinitionXml(procDef.getId());

        //启动流程
        ProcessInstance procInst = runtimeService.startProcessInstanceById(procDef.getId());
        //查询第一个任务
        Task firstTask = taskService.createTaskQuery()
                .processInstanceId(procInst.getId()).singleResult();
        //设置流程变量
        Map<String, Object> varMap = ImmutableMap.of("money", 1000);
        //完成第一个任务
        taskService.complete(firstTask.getId(), varMap);
        //查询第二个任务
        Task secondTask = taskService.createTaskQuery()
                .processInstanceId(procInst.getId()).singleResult();
        log.info("第二个节点任务名称为：{}", secondTask.getName());
        //完成第二个任务
        taskService.complete(secondTask.getId());
    }
}
