package com.bpm.example.intermediateevent.demo;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class RunSignalIntermediateEventProcessDemo extends FlowableEngineUtil {
    @Test
    public void runSignalIntermediateEventProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/SignalIntermediateEventProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("signalIntermediateEventProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询并完成第一个任务
        Task firstTask = taskQuery.singleResult();
        log.info("第一个用户任务为：{}", firstTask.getName());
        taskService.complete(firstTask.getId());
        //查询并完成第二个任务
        Task secondTask = taskQuery.singleResult();
        log.info("第二个用户任务为：{}", secondTask.getName());
        taskService.complete(secondTask.getId());
        //根据流程实例查询任务
        List<Task> tasks = taskQuery.list();
        log.info("当前流程所处的用户任务有：{}", tasks.stream().map(Task::getName)
                .collect(Collectors.joining("，")));
    }
}