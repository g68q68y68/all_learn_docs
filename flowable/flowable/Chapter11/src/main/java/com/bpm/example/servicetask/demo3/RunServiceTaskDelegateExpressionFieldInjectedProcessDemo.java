package com.bpm.example.servicetask.demo3;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.servicetask.demo3.delegate
        .CalculationDelegateExpressionFieldInjectedJavaDelegate;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Test;

import java.util.Map;

@Slf4j
public class RunServiceTaskDelegateExpressionFieldInjectedProcessDemo
        extends FlowableEngineUtil {
    @Test
    public void runServiceTaskDelegateExpressionFieldInjectedProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes" +
                "/ServiceTaskDelegateExpressionFieldInjectedProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService.startProcessInstanceByKey
                ("serviceTaskDelegateExpressionFieldInjectedProcess");
        //查询第一个任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(procInst.getId()).singleResult();
        //初始化流程变量
        Map<String, Object> varMap = ImmutableMap.of("unitPrice", 100.0, "quantity", 4,
                "description", "此次采购了4个单价100元的办公耗材");
        //初始化瞬时变量
        Map<String, Object> traVarMap = ImmutableMap
                .of("calculationDelegateExpressionFieldInjectedJavaDelegate",
                        new CalculationDelegateExpressionFieldInjectedJavaDelegate());
        //办理第一个任务
        taskService.complete(task.getId(), varMap, traVarMap);
        //查询历史流程变量
        HistoricVariableInstance historicVariableInstance = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(procInst.getId())
                .variableName("totalAmount")
                .singleResult();
        log.info("totalAmount的值为：{}", historicVariableInstance.getValue());
    }
}