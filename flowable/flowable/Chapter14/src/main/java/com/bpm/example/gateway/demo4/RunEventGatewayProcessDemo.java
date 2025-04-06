package com.bpm.example.gateway.demo4;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class RunEventGatewayProcessDemo extends FlowableEngineUtil {
    @Test
    public void runEventGatewayProcessDemo() throws Exception {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.job.xml");
        //部署流程
        deployResource("processes/EventGatewayProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("eventGatewayProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询并办理第一个任务
        Task task1 = taskQuery.singleResult();
        taskService.complete(task1.getId());
        //查询执行实例
        List<Execution> executionList = runtimeService.createExecutionQuery()
                .processInstanceId(procInst.getId())
                .onlyChildExecutions().list();
        log.info("第一个任务办理后，当前流程所处节点为：{}", executionList.stream()
                .map(Execution::getActivityId).collect(Collectors.joining(",")));

        //等待5分钟
        Thread.sleep(1000 * 60 * 5);
        //触发信号
        runtimeService.signalEventReceived("alert");
        Task task2 = taskQuery.singleResult();
        log.info("触发信号后，当前流程所处用户任务名称：{}", task2.getName());
        //办理任务
        taskService.complete(task2.getId());
    }
}