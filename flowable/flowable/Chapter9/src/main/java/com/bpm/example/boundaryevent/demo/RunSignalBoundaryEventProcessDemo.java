package com.bpm.example.boundaryevent.demo;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Test;

import java.util.List;

@Slf4j
public class RunSignalBoundaryEventProcessDemo extends FlowableEngineUtil {
    @Test
    public void runSignalBoundaryEventProcessDemo() throws Exception {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition procDef
                = deployResource("processes/SignalBoundaryEventProcess.bpmn20.xml");

        //启动两个流程实例
        ProcessInstance procInst1 = runtimeService
                .startProcessInstanceById(procDef.getId());
        log.info("第1个流程实例的编号为：{}", procInst1.getId());
        ProcessInstance procInst2 = runtimeService
                .startProcessInstanceById(procDef.getId());
        log.info("第2个流程实例的编号为：{}", procInst2.getId());

        //将实例一进行到确认合同
        Task task1OfProcInst1 = taskService.createTaskQuery()
                .processInstanceId(procInst1.getId()).singleResult();
        taskService.complete(task1OfProcInst1.getId());
        Task task2OfProcInst1 = taskService.createTaskQuery()
                .processInstanceId(procInst1.getId()).singleResult();
        log.info("第1个流程实例当前所在用户任务为：{}", task2OfProcInst1.getName());
        //将实例二进行到确认合同
        Task task1OfProcInst2 = taskService.createTaskQuery()
                .processInstanceId(procInst2.getId()).singleResult();
        taskService.complete(task1OfProcInst2.getId());
        Task task2OfProcInst2 = taskService.createTaskQuery()
                .processInstanceId(procInst2.getId()).singleResult();
        log.info("第2个流程实例当前所在用户任务为：{}", task2OfProcInst2.getName());
        //发送合同变更信号
        runtimeService.signalEventReceived("修改合同");
        log.info("发送合同变更信号完成");
        //根据流程定义查询任务
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionId(procDef.getId()).list();
        for (Task task : tasks) {
            log.info("编号为{}的流程实例当前所在用户任务为：{}", task.getProcessInstanceId(), task.getName());
        }
    }
}