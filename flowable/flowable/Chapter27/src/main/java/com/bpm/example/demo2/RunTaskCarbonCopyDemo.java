package com.bpm.example.demo2;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo2.service.TaskCarbonCopyService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.Arrays;

@Slf4j
public class RunTaskCarbonCopyDemo extends FlowableEngineUtil {
    @Test
    public void runTaskCarbonCopyDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/TaskCarbonCopyProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("taskCarbonCopyProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询并办理第一个任务
        Task firstTask = taskQuery.singleResult();
        taskService.complete(firstTask.getId());
        //查询第二个任务
        Task secondTask = taskQuery.singleResult();
        //分别为两个任务添加知会人
        TaskCarbonCopyService taskCarbonCopyService
                = new TaskCarbonCopyService(managementService, taskService, historyService);
        taskCarbonCopyService.executeTaskCarbonCopy(firstTask.getId(),
                Arrays.asList("huhaiqin", "liuxiaopeng"));
        taskCarbonCopyService.executeTaskCarbonCopy(secondTask.getId(),
                Arrays.asList("hebo"));
        //查询liuxiaopeng的已办任务数
        long hisTaskNum = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee("liuxiaopeng").processInstanceId(procInst.getId())
                .count();
        //查询liuxiaopeng的历史知会数
        long hisCarbonCopyNum = taskCarbonCopyService
                .getHistoricCarbonCopyTasks("liuxiaopeng")
                .stream().filter(hisTask
                        -> hisTask.getProcessInstanceId().equals(procInst.getId()))
                .count();
        log.info("{}在流程实例{}下的已办任务数为{}，历史知会数为{}", "liuxiaopeng",
                procInst.getId(), hisTaskNum, hisCarbonCopyNum);
        //查询hebo的待办任务数
        long taskNum = taskQuery.taskCandidateOrAssigned("hebo").count();
        //查询hebo的知会数
        long carbonCopyNum = taskCarbonCopyService.getCarbonCopyTasks("hebo")
                .stream().filter(task -> task.getProcessInstanceId().equals(procInst.getId()))
                .count();
        log.info("{}在流程实例{}下的待办任务数为{}，知会数为{}", "hebo", procInst.getId(),
                taskNum, carbonCopyNum);
    }
}
