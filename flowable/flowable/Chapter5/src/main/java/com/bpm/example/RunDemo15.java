package com.bpm.example;

import com.bpm.example.common.util.FlowableEngineUtil;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunDemo15 extends FlowableEngineUtil {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void main(String[] args) {
        RunDemo15 demo = new RunDemo15();
        demo.runDemo();
    }

    private void runDemo() {
        //初始化工作流引擎配置
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程定义
        ProcessDefinition processDefinition =
                deployResource("processes/SimpleProcess.bpmn20.xml");
        System.out.println("流程定义ID为：" + processDefinition.getId() + "，流程名称为："
                + processDefinition.getName());
        //启动流程
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceById(processDefinition.getId());
        System.out.println("流程实例ID为：" + processInstance.getId()
                + "，流程定义key为：" + processInstance.getProcessDefinitionKey());
        //查询第一个任务
        Task firstTask = taskService.createTaskQuery().
                processInstanceId(processInstance.getId()).singleResult();
        System.out.println("第一个任务ID为：" + firstTask.getId() + "，任务名称为：" +
                firstTask.getName());
        taskService.setAssignee(firstTask.getId(), "huhaiqin");
        //完成第一个任务
        taskService.complete(firstTask.getId());
        System.out.println("第一个任务办理完成！");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //查询第二个任务
        Task secondTask = taskService.createTaskQuery().processInstanceId
                (processInstance.getId()).singleResult();
        System.out.println("第二个任务ID为：" + secondTask.getId() + "，任务名称为：" +
                secondTask.getName());
        taskService.setAssignee(secondTask.getId(), "hebo");
        //设置审批意见
        Map<String, Object> variables = new HashMap<>();
        variables.put("task_审批_outcome", "agree");
        //完成第二个任务
        taskService.complete(secondTask.getId(), variables);
        System.out.println("第二个任务办理完成！");
        //查询流程执行历史
        HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(processInstance.getId())
                        .singleResult();
        System.out.println("流程实例开始时间：" + dateFormat.format(historicProcessInstance.
                getStartTime()) + ",结束时间：" + dateFormat.format(historicProcessInstance.getEndTime()));
        //查询活动实例
        List<HistoricActivityInstance> historicActivityInstances =
                historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId(processInstance.getId())
                        .orderByHistoricActivityInstanceStartTime().asc()
                        .list();
        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            System.out.println("活动实例[" + historicActivityInstance.getActivityName() + "], 开始时间：" + dateFormat.format(historicActivityInstance.getStartTime()) + ",结束时间：" +
                    dateFormat.format(historicActivityInstance.getEndTime()));
        }
        //查询任务实例
        List<HistoricTaskInstance> historicTaskInstances =
                historyService.createHistoricTaskInstanceQuery()
                        .processInstanceId(processInstance.getId())
                        .orderByHistoricTaskInstanceStartTime().asc()
                        .list();
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
            System.out.println("任务实例[" + historicTaskInstance.getName() + "]的办理人为："
                    + historicTaskInstance.getAssignee()
            );
        }
        //查询流程变量
        List<HistoricVariableInstance> historicVariableInstances =
                historyService.createHistoricVariableInstanceQuery()
                        .processInstanceId(processInstance.getId())
                        .list();
        for (HistoricVariableInstance historicVariableInstance : historicVariableInstances) {
            System.out.println("流程变量[" + historicVariableInstance.getVariableName() + "] 的值为：" + historicVariableInstance.getValue()
            );
        }
    }
}

