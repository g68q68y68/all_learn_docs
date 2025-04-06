package com.bpm.example.demo1;

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
public class RunHttpServiceTaskProcessDemo extends FlowableEngineUtil {
    @Test
    public void runHttpServiceTaskProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/HttpServiceTaskProcess.bpmn20.xml");

        //发起流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("httpServiceTaskProcess");
        //设置流程变量
        Map<String, Object> varMap = ImmutableMap.of("ip", "114.247.88.20");
        //查询第一个任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(procInst.getId()).singleResult();
        //办理第一个任务
        taskService.complete(task.getId(), varMap);
        //查询历史变量
        List<HistoricVariableInstance> variableList = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(procInst.getId()).list();
        variableList.stream().forEach((variable) ->
                log.info("流程变量名：{}，变量类型：{}，变量值：{}", variable.getVariableName(),
                        variable.getVariableTypeName(), variable.getValue()));
    }
}