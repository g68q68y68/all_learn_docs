package com.bpm.example.executionlistener.demo;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.executionlistener.demo.bean.MyProcessExecutionListenerBean;
import com.bpm.example.executionlistener.demo.listener.MySequenceFlowExecutionListener;
import com.bpm.example.executionlistener.demo.listener.MyUserTaskExecutionListener;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Test;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
public class RunExecutionListenerProcessDemo extends FlowableEngineUtil {
    @Test
    public void runExecutionListenerProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/ExecutionListenerProcess.bpmn20.xml");

        //将监听器bean放入到流程变量中
        Map<String, Object> varMap1 = ImmutableMap.of("nowTime1", new Date(),
                "myProcessExecutionListenerBean", new MyProcessExecutionListenerBean(),
                "mySequenceFlowExecutionListenerBean", new MySequenceFlowExecutionListener(),
                "myUserTaskExecutionListenerBean", new MyUserTaskExecutionListener());
        //启动流程实例
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("executionListenerProcess", varMap1);
        //查询任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(procInst.getId()).singleResult();
        Map<String, Object> varMap2 = ImmutableMap.of("nowTime2", new Date());
        //办理任务
        taskService.complete(task.getId(), varMap2);
        //查询并打印流程变量
        List<HistoricVariableInstance> hisVars = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(procInst.getId()).list();
        hisVars.stream().forEach(hisVar -> log.info("流程变量名：{}，变量值：{}",
                hisVar.getVariableName(), hisVar.getValue()));
    }
}
