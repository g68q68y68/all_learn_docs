package com.bpm.example;

import com.bpm.example.common.util.FlowableEngineUtil;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.text.SimpleDateFormat;

public class RunDemo6 extends FlowableEngineUtil {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void main(String[] args) {
        RunDemo6 demo = new RunDemo6();
        demo.runDemo();
    }

    private void runDemo() {
        //初始化工作流引擎
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition processDefinition =
                deployResource("processes/SimpleProcess2.bpmn20.xml");
        System.out.println("流程定义ID为：" + processDefinition.getId() + "，流程定义key为："
                + processDefinition.getKey());
        //根据流程定义ID发起流程
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceById(processDefinition.getId());
        System.out.println("流程实例的ID为：" + processInstance.getId());
        //查询第一个任务
        Task firstTask =
                taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("第一个任务ID为：" + firstTask.getId() + "，任务名称为："
                + firstTask.getName());
        taskService.setAssignee(firstTask.getId(), "huhaiqin");
        //完成第一个任务
        taskService.complete(firstTask.getId());
        System.out.println("第一个任务办理完成！");

        Execution execution = runtimeService.createExecutionQuery()
                .processInstanceId(processInstance.getId()).onlyChildExecutions().singleResult();
        System.out.println("当前执行实例ID：" + execution.getId());
        runtimeService.trigger(execution.getId());
        System.out.println("触发机器节点，继续流转！");
        //查询流程执行历史
        HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(processInstance.getId())
                        .singleResult();
        System.out.println("流程实例开始时间：" + dateFormat.format(historicProcessInstance. getStartTime()) + ",结束时间：" + dateFormat.format(historicProcessInstance.getEndTime()));
    }
}

