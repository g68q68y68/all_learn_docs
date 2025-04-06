package com.bpm.example.demo1;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

@Slf4j
public class RunFirstDemo {
    @Test
    public void runFirstDemo() {
        //创建工作流引擎
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        //获取流程存储服务
        RepositoryService repositoryService = engine.getRepositoryService();
        //获取运行时服务
        RuntimeService runtimeService = engine.getRuntimeService();
        //获取流程任务
        TaskService taskService = engine.getTaskService();
        //部署流程
        repositoryService.createDeployment()
                .addClasspathResource("processes/LeaveApplyProcess.bpmn20.xml")
                .deploy();

        //启动流程
        runtimeService.startProcessInstanceByKey("LeaveApplyProcess");
        TaskQuery taskQuery = taskService.createTaskQuery();
        //查询第一个任务
        Task firstTask = taskQuery.taskAssignee("liuxiaopeng").singleResult();
        //完成第一个任务
        taskService.complete(firstTask.getId());
        log.info("用户任务{}办理完成，办理人为：{}", firstTask.getName(), firstTask.getAssignee());
        //查询第二个任务
        Task secondTask = taskQuery.taskAssignee("hebo").singleResult();
        log.info("用户任务{}办理完成，办理人为：{}", secondTask.getName(), secondTask.getAssignee());
        //完成第二个任务（流程结束）
        taskService.complete(secondTask.getId());
        //查询任务数
        long taskNum = taskService.createTaskQuery().count();
        log.info("流程结束后，剩余任务数：{}", taskNum);

        //关闭工作流引擎
        engine.close();
    }
}

