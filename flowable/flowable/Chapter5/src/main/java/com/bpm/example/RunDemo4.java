package com.bpm.example;

import com.bpm.example.common.util.FlowableEngineUtil;
import org.flowable.engine.repository.ProcessDefinition;

public class RunDemo4 extends FlowableEngineUtil {
    public static void main(String[] args) {
        RunDemo4 demo = new RunDemo4();
        demo.runDemo();
    }

    public void runDemo() {
        //初始化工作流引擎
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition processDefinition =
                deployResource("processes/SimpleProcess.bpmn20.xml");
        //查询流程定义状态
        queryProcessDefinition(processDefinition.getId());
        //挂起流程定义
        repositoryService.suspendProcessDefinitionById(processDefinition.getId());
        System.out.println("挂起流程定义！流程定义ID=" + processDefinition.getId());
        //再次查询流程定义状态
        queryProcessDefinition(processDefinition.getId());
        //激活流程定义
        repositoryService.activateProcessDefinitionById(processDefinition.getId());
        System.out.println("激活流程定义！流程定义ID=" + processDefinition.getId());
        //再次查询流程定义状态
        queryProcessDefinition(processDefinition.getId());
    }

    private void queryProcessDefinition(String processDefinitionId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        System.out.println("***流程定义ID=" + processDefinition.getId()
                + " , 流程定义key=" + processDefinition.getKey()
                + " , 是否挂起=" + processDefinition.isSuspended());
    }
}

