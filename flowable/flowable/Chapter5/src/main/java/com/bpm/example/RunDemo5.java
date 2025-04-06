package com.bpm.example;

import com.bpm.example.common.util.FlowableEngineUtil;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;

public class RunDemo5 extends FlowableEngineUtil {
    public static void main(String[] args) {
        RunDemo5 demo = new RunDemo5();
        demo.runDemo();
    }

    private void runDemo() {
        //初始化工作流引擎
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition processDefinition =
                deployResource("processes/SimpleProcess.bpmn20.xml");
        System.out.println("流程定义ID为：" + processDefinition.getId() + "，流程定义key为："
                + processDefinition.getKey());
        //根据流程定义ID发起流程
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceById(processDefinition.getId());
        queryProcessInstance(processInstance.getId());
        //根据流程定义key发起流程
        ProcessInstance processInstance2 = runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey(processDefinition.getKey())
                .name("SimpleProcessInstance")
                .start();
        queryProcessInstance(processInstance2.getId());
    }

    private void queryProcessInstance(String processInstanceId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        System.out.println("流程实例ID为：" + processInstance.getId()
                + "，流程定义ID为：" + processInstance.getProcessDefinitionId()
                + "，流程实例名称为：" + processInstance.getName()
        );
    }
}

