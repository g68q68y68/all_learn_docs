package com.bpm.example.tasklistener.demo;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.tasklistener.demo.bean.MyTaskListenerBean;
import com.bpm.example.tasklistener.demo.listener.MyTaskListener2;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RunTaskListenerProcessDemo extends FlowableEngineUtil {
    @Test
    public void runTaskListenerProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/TaskListenerProcess.bpmn20.xml");

        //初始化流程变量
        Map varMap = ImmutableMap.of("myTaskListenerBean", new MyTaskListenerBean(),
                "myTaskListener2", new MyTaskListener2());
        //启动流程实例
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("taskListenerProcess", varMap);
        //查询任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(procInst.getId()).singleResult();
        //设置任务办理人
        task.setAssignee("liuxiaopeng");
        //办理任务
        taskService.complete(task.getId());
    }
}
