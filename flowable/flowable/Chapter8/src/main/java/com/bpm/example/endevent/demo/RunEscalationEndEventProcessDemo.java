package com.bpm.example.endevent.demo;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

@Slf4j
public class RunEscalationEndEventProcessDemo extends FlowableEngineUtil {
    @Test
    public void runEscalationEndEventProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/EscalationEndEventProcess.bpmn20.xml");

        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("escalationEndEventProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询第一个任务
        Task task1 = taskQuery.singleResult();
        log.info("第一个任务名称为: {}", task1.getName());
        //完成第一个任务
        taskService.complete(task1.getId());
        //查询第二个任务
        Task task2 = taskQuery.singleResult();
        log.info("第二个任务名称为: {}", task2.getName());
        //设置流程变量并完成第二个任务
        Map variables = Collections.singletonMap("needEscalate", true);
        taskService.complete(task2.getId(), variables);
        //查询第三个任务
        Task task3 = taskQuery.singleResult();
        log.info("第三个任务名称为: {}", task3.getName());
        //完成第三个任务
        taskService.complete(task3.getId());
    }
}
