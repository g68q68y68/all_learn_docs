package com.bpm.example.intermediateevent.demo;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

@Slf4j
public class RunTimerIntermediateCatchingEventProcessDemo extends FlowableEngineUtil {
    @Test
    public void runTimerIntermediateCatchingEventProcessDemo() throws Exception {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.job.xml");
        //部署流程
        deployResource("processes/TimerIntermediateCatchingEventProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("timerIntermediateCatchingEventProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询第一个任务
        Task firstTask = taskQuery.singleResult();
        log.info("第一个任务为：{}", firstTask.getName());
        //完成第一个任务
        taskService.complete(firstTask.getId());
        //暂停6分钟
        log.info("暂停6分钟");
        Thread.sleep(1000 * 60 * 6);
        //查询第二个任务节点
        Task secondTask = taskQuery.singleResult();
        log.info("第二个任务为：{}", secondTask.getName());
    }
}