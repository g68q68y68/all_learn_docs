package com.bpm.example.businessruletask.demo;

import com.alibaba.fastjson.JSON;
import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.businessruletask.demo.model.CostCalculation;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class RunBusinessRuleTaskProcessDemo extends FlowableEngineUtil {
    @Test
    public void runBusinessRuleTaskProcessDemo() {
        //加载Activiti配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.drools.xml");
        //部署流程
        deployResources("processes/BusinessRuleTaskProcess.bpmn20.xml",
                "rules/DiscountCalculation.drl");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("businessRuleTaskProcess");
        //查询第一个任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(procInst.getId()).singleResult();
        //初始化流程变量
        CostCalculation costCalculation = new CostCalculation();
        costCalculation.setOriginalTotalPrice(6666);
        Map<String, Object> varMap = ImmutableMap
                .of("myCostCalculation", costCalculation);
        //办理第一个任务
        taskService.complete(task.getId(), varMap);
        //查询并打印流程变量
        List<HistoricVariableInstance> hisVars = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(procInst.getId()).list();
        hisVars.stream().forEach((hisVar) -> log.info("流程变量名：{}，变量值：{}",
                hisVar.getVariableName(),JSON.toJSONString(hisVar.getValue())));
    }
}