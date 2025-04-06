package com.bpm.example.startevent.demo;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Test;

@Slf4j
public class RunMessageStartEventProcessDemo extends FlowableEngineUtil {
    @Test
    public void runMessageStartEventProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/MessageStartEventProcess.bpmn20.xml");

        //通过API发起流程
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByMessage("dataReportingMessage");
        //查询任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId()).singleResult();
        log.info("当前任务名称为: {}", task.getName());
        //完成任务
        taskService.complete(task.getId());
    }
}
