package com.bpm.example.receivetask.demo;

import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RunReceiveTaskProcessDemo extends FlowableEngineUtil {
    @Test
    public void runReceiveTaskProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/ReceiveTaskProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("receiveTaskProcess");
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
        //查询执行到此接收任务的执行实例
        Execution execution = runtimeService.createExecutionQuery()
                .processInstanceId(procInst.getId()) //使用流程实例ID查询
                .activityId("receiveTask1")  //当前活动的id，对应ReceiveTask的节点id
                .singleResult();
        //设置流程变量
        Map<String, Object> varMap = ImmutableMap.of("result", "账号成功激活！");
        //触发流程离开接收任务继续往下执行
        runtimeService.trigger(execution.getId(), varMap);
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
