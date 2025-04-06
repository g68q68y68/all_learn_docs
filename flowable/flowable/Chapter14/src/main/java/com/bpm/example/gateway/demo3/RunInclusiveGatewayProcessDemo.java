package com.bpm.example.gateway.demo3;

import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class RunInclusiveGatewayProcessDemo extends FlowableEngineUtil {
    @Test
    public void runInclusiveGatewayProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/InclusiveGatewayProcess.bpmn20.xml");

        //启动流程
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("inclusiveGatewayProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId());
        //查询请假申请任务
        Task applyTask = taskQuery.singleResult();
        log.info("流程发起后，第一个用户任务名称为：{}", applyTask.getName());
        //设置leaveDays变量值
        Map<String, Object> varMap = ImmutableMap.of("leaveDays", 5);
        //完成请假申请任务
        taskService.complete(applyTask.getId(), varMap);
        List<Task> taskList1 = taskQuery.list();
        //查询任务个数
        log.info("第一个任务办理后，当前流程的任务个数为{}，分别为：{}", taskList1.size(),
                taskList1.stream().map(Task::getName).collect(Collectors.joining("，")));
        //办理任务
        taskList1.stream().forEach(task -> {
            taskService.complete(task.getId());
            log.info("办理完成用户任务：{}", task.getName());
        });
        List<Task> taskList2 = taskQuery.list();
        //查询任务个数
        log.info("办理完前{}个任务后，流程当前任务个数为{}，分别为：{}", taskList1.size(),
                taskList2.size(),
                taskList2.stream().map(Task::getName)
                        .collect(Collectors.joining("，")));
        //办理任务
        taskList2.stream().forEach(task -> {
            taskService.complete(task.getId());
            log.info("办理完成用户任务：{}", task.getName());
        });
        //查询剩余任务个数
        log.info("最后剩余的用户任务数为{}", taskQuery.list().size());
    }
}