package com.bpm.example.gateway.demo2;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class RunParallelGatewayProcessDemo extends FlowableEngineUtil {
    @Test
    public void runParallelGatewayProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/ParallelGatewayProcess.bpmn20.xml");

        //启动流程
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("parallelGatewayProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId());
        //查询任务
        List<Task> taskList1 = taskQuery.list();
        log.info("流程发起后，当前任务个数为：{}，分别为：{}", taskList1.size(),
                taskList1.stream().map(Task::getName)
                        .collect(Collectors.joining("，")));
        //办理前taskList1.size()个用户任务
        taskList1.stream().forEach(task -> {
            taskService.complete(task.getId());
            log.info("办理完成用户任务：{}", task.getName());
        });
        List<Task> taskList2 = taskQuery.list();
        //查询任务
        log.info("办理完前{}个任务后，当前流程的任务个数为{}，分别为：{}", taskList1.size(),
                taskList2.size(), taskList2.stream().map(Task::getName)
                        .collect(Collectors.joining(",")));
        //办理taskList2.size()个用户任务
        taskList2.stream().forEach(task -> {
            taskService.complete(task.getId());
            log.info("办理完成用户任务：{}", task.getName());
        });
        //查询剩余任务
        List<Task> taskList3 = taskQuery.list();
        log.info("最后剩余的用户任务数为：{}", taskList3.size());
    }
}