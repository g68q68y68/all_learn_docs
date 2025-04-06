package com.bpm.example.eventlistener.demo;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Test;

@Slf4j
public class RunEventListenersProcessDemo extends FlowableEngineUtil {
    @Test
    public void runEventListenersProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.eventlistener.xml");
        //部署流程
        deployResource("processes/EventListenersProcess.bpmn20.xml");

        //启动流程实例
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("eventListenersProcess");
        //查询任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(procInst.getId()).singleResult();
        //办理任务
        taskService.complete(task.getId());
    }
}