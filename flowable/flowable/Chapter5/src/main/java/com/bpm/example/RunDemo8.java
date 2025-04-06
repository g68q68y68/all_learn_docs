package com.bpm.example;

import com.bpm.example.common.util.FlowableEngineUtil;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.text.SimpleDateFormat;
import java.util.List;

public class RunDemo8 extends FlowableEngineUtil {
    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void main(String[] args) {
        RunDemo8 demo = new RunDemo8();
        demo.runDemo();
    }

    private void runDemo() {
        //初始化工作流引擎
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程定义
        ProcessDefinition processDefinition =
                deployResource("processes/SimpleProcess.bpmn20.xml");
        System.out.println("流程定义ID为：" + processDefinition.getId() + "，流程定义key为：" +
                processDefinition.getKey());
        //根据流程定义ID发起流程
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceById(processDefinition.getId());
        System.out.println("发起了一个流程实例，流程实例ID为：" + processInstance.getId());
        //查询第一个任务
        Task firstTask = taskService.createTaskQuery().processInstanceId
                (processInstance.getId()).singleResult();
        System.out.println("第一个任务ID为：" + firstTask.getId() + "，任务名称为：" +
                firstTask.getName());
        //完成第一个任务
        taskService.complete(firstTask.getId());
        System.out.println("第一个任务办理完成！没有进行权限控制");
        //查询第二个任务
        Task secondTask = taskService.createTaskQuery().processInstanceId
                (processInstance.getId()).singleResult();
        System.out.println("第二个任务ID为：" + secondTask.getId() + "，任务名称为：" +
                secondTask.getName());
        //设置任务与人员的关系
        taskService.addCandidateUser(secondTask.getId(), "hebo");
        taskService.addCandidateUser(secondTask.getId(), "liuxiaopeng");
        taskService.addUserIdentityLink(secondTask.getId(), "wangjunlin", "participant");
        //办理任务
        MyTaskService myTaskService = new MyTaskService(taskService);
        myTaskService.complete(secondTask.getId(), "huhaiqin");
        myTaskService.complete(secondTask.getId(), "wangjunlin");
        myTaskService.complete(secondTask.getId(), "liuxiaopeng");
        myTaskService.complete(secondTask.getId(), "hebo");
        //因为流程已结束，所以只能通过历史服务来获取任务实例
        System.out.println("=====查询流程实例(ID=" + processInstance.getId() + ")的任务办理情况=====");
        List<HistoricTaskInstance> historicTaskInstances =
                historyService.createHistoricTaskInstanceQuery()
                        .processInstanceId(processInstance.getId())
                        .orderByHistoricTaskInstanceStartTime().asc()
                        .list();
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
            System.out.println("任务[" + historicTaskInstance.getName()
                    + "(ID=" + historicTaskInstance.getId() + ")]的办理人为：" +
                    historicTaskInstance.getAssignee()
                    + "，开始时间：" +
                    dateFormat.format(historicTaskInstance.getStartTime())
                    + "，结束时间：" + dateFormat.format(historicTaskInstance.getEndTime())
            );
        }
    }
}

