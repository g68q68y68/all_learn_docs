package com.bpm.example;

import com.bpm.example.common.util.FlowableEngineUtil;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;

public class RunDemo3 extends FlowableEngineUtil {
    public static void main(String[] args) {
        RunDemo3 demo = new RunDemo3();
        demo.runDemo();
    }

    private void runDemo() {
        //初始化工作流引擎
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition processDefinition =
                deployResource("processes/SimpleProcess.bpmn20.xml");
        //查询流程定义状态
        queryProcessDefinition(processDefinition.getId());
        //发起流程实例
        startProcessInstance(processDefinition.getId());
        //挂起流程定义
        repositoryService.suspendProcessDefinitionById(processDefinition.getId());
        System.out.println("挂起流程定义！流程定义ID=" + processDefinition.getId());
        //再次查询流程定义状态
        queryProcessDefinition(processDefinition.getId());
        //再次发起流程实例
        startProcessInstance(processDefinition.getId());
    }

    //查询流程定义状态
    private void queryProcessDefinition(String processDefinitionId) {
        ProcessDefinition processDefinition =
                repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(processDefinitionId)
                        .singleResult();
        System.out.println("***流程定义ID=" + processDefinition.getId()
                + " , 流程定义key=" + processDefinition.getKey()
                + " , 是否挂起=" + processDefinition.isSuspended());
    }

    //根据流程定义ID发起流程
    private void startProcessInstance(String processDefinitionId) {
        try {
            ProcessInstance processInstance =
                    runtimeService.startProcessInstanceById(processDefinitionId);
            System.out.println("发起流程实例成功。流程实例ID为：" + processInstance.getId()
                    + "，流程定义ID为：" + processInstance.getProcessDefinitionId()
                    + "，流程定义key为：" + processInstance.getProcessDefinitionKey());
        } catch (Exception e) {
            System.out.println("发起流程实例失败。" + e.getMessage());
        }
    }
}

