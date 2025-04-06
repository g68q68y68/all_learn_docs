package com.bpm.example.startevent.demo;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

@Slf4j
public class RunTimerStartEventProcessDemo extends FlowableEngineUtil {
    @Test
    public void runTimerStartEventProcessDemo() throws Exception {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.job.xml");
        //部署流程
        ProcessDefinition procDef
                = deployResource("processes/TimerStartEventProcess.bpmn20.xml");

        // 暂停90秒
        Thread.sleep(1000 * 90);

        //查询任务个数
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processDefinitionId(procDef.getId());
        log.info("流程实例中任务个数为: {}", taskQuery.count());
        Task task = taskQuery.singleResult();
        log.info("当前任务名称为: {}", task.getName());
        //完成任务
        taskService.complete(task.getId());
    }
}