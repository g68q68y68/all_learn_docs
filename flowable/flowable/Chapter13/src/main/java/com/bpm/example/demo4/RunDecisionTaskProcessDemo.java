package com.bpm.example.demo4;

import com.alibaba.fastjson.JSON;
import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Test;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class RunDecisionTaskProcessDemo extends FlowableEngineUtil {
    @Test
    public void runDecisionTaskProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.dmn.xml");
        //部署流程和决策表
        deployResources("processes/DecisionTaskProcess.bpmn20.xml",
                "dmn/DiscountCalculationDecision.dmn");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("decisionTaskProcess");
        //查询第一个任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(procInst.getId()).singleResult();
        //初始化流程变量
        Map<String, Object> varMap = ImmutableMap.of("originalTotalPrice", 6666);
        //办理第一个任务
        taskService.complete(task.getId(), varMap);
        //查询并打印流程变量
        List<HistoricVariableInstance> hisVariables  = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(procInst.getId()).list();
        hisVariables.stream().forEach(hisVariable -> log.info("流程变量名：{}，变量值：{}",
                hisVariable.getVariableName(), hisVariable.getValue()));
    }
}