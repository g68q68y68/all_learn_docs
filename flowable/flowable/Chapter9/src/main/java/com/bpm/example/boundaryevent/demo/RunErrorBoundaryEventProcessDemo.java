package com.bpm.example.boundaryevent.demo;

import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

@Slf4j
public class RunErrorBoundaryEventProcessDemo extends FlowableEngineUtil {
    @Test
    public void runErrorBoundaryEventProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/ErrorBoundaryEventProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("errorBoundaryEventProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询并完成第一个任务
        Task firstTask = taskQuery.singleResult();
        log.info("第一个用户任务为：{}", firstTask.getName());
        Map<String, Object> varMap = ImmutableMap.of("healthCodeStatus", "red");
        taskService.complete(firstTask.getId(), varMap);
        //查询第二个任务
        Task secondTask = taskQuery.singleResult();
        log.info("第二个用户任务为: {}", secondTask.getName());
        //完成第二个任务
        taskService.complete(secondTask.getId());
    }
}