package com.bpm.example.manualtask.demo;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.text.SimpleDateFormat;

@Slf4j
public class RunManualTaskProcessDemo extends FlowableEngineUtil {
    @Test
    public void runManualTaskProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/ManualTaskProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("manualTaskProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询并完成第一个任务
        Task firstTask = taskQuery.singleResult();
        log.info("即将完成第一个任务，当前任务名称：{}", firstTask.getName());
        taskService.complete(firstTask.getId());
        //查询并完成第二个任务
        Task secondTask = taskQuery.singleResult();
        log.info("即将完成第二个任务，当前任务名称：{}", secondTask.getName());
        taskService.complete(secondTask.getId());
        //查询历史流程实例
        HistoricProcessInstance hisProcInst = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(procInst.getId())
                .singleResult();
        if (hisProcInst.getEndTime() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            log.info("当前流程已结束，结束时间：{}", dateFormat.format(hisProcInst.getEndTime()));
        }
    }
}