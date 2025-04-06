package com.bpm.example;

import com.bpm.example.common.util.FlowableEngineUtil;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.job.api.Job;
import org.flowable.task.api.Task;

import java.text.SimpleDateFormat;
import java.util.List;

public class RunDemo11 extends FlowableEngineUtil {
    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void main(String[] args) {
        RunDemo11 demo = new RunDemo11();
        demo.runDemo();
    }

    private void runDemo() {
        //初始化工作流引擎
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition processDefinition =
                deployResource("processes/SimpleProcess3.bpmn20.xml");
        System.out.println("流程定义ID为：" + processDefinition.getId() + "，流程名称为："
                + processDefinition.getName());
        //发起一个流程实例
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceById(processDefinition.getId());
        System.out.println("流程实例ID为：" + processInstance.getId() + "，流程定义key为："
                + processInstance.getProcessDefinitionKey());
        //查询第一个任务
        Task firstTask = taskService.createTaskQuery().processInstanceId(
                processInstance.getId()).singleResult();
        //完成第一个任务
        taskService.complete(firstTask.getId());
        // 查询流程实例ID对应的job任务列表
        List<Job> timerJobs = managementService.createTimerJobQuery()
                .processInstanceId(processInstance.getId())
                .list();
        for (Job job : timerJobs) {
            System.out.println("定时任务" + job.getId() + "类型：" + job.getJobType()
                    + "，定时执行时间：" + dateFormat.format(job.getDuedate()));
            managementService.moveTimerToExecutableJob(job.getId());
            System.out.println("----立即执行任务" + job.getId());
            managementService.executeJob(job.getId());
        }
        //查询流程执行历史
        HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(processInstance.getId())
                        .singleResult();
        System.out.println("流程实例开始时间：" + dateFormat.format(historicProcessInstance.
                getStartTime()) + ",结束时间：" + dateFormat.format(historicProcessInstance.getEndTime()));
    }
}

