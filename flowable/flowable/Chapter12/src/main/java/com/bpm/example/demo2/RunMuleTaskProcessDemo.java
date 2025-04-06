package com.bpm.example.demo2;
import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Test;

import java.util.List;
import java.util.Map;

@Slf4j
public class RunMuleTaskProcessDemo extends FlowableEngineUtil {
    @Test
    public void runMuleTaskProcessDemo() throws Exception {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.mule.xml");
        //部署流程
        deployResource("processes/MuleTaskProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("muleTaskProcess");
        //设置流程变量
        Map<String, Object> varMap = ImmutableMap.of("ip", "119.99.130.0");
        //查询并办理第一个任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(procInst.getId()).singleResult();
        taskService.complete(task.getId(), varMap);
        //查询并打印流程变量
        List<HistoricVariableInstance> hisVars = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(procInst.getId()).list();
        hisVars.stream().forEach((hisVar) -> log.info("流程变量名：{}，变量值：{}",
                hisVar.getVariableName(), hisVar.getValue()));
    }
}