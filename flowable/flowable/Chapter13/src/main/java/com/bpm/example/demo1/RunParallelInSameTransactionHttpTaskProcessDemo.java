package com.bpm.example.demo1;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class RunParallelInSameTransactionHttpTaskProcessDemo extends FlowableEngineUtil {
    @Test
    public void runParallelInSameTransactionHttpTaskProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition processDefinition = deployResource("processes/ParallelInSameTransactionHttpTaskProcess.bpmn20.xml");

        //设置流程变量
        Map variables = Collections.singletonMap("ip", "114.247.88.20");
        //发起流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceById(processDefinition.getId(), variables);
        //查询历史变量
        List<HistoricVariableInstance> variableList = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(procInst.getId()).list();
        variableList.stream().forEach((variable) ->
                log.info("流程变量名：{}，变量类型：{}，变量值：{}", variable.getVariableName(),
                        variable.getVariableTypeName(),
                        variable.getValue()));
    }
}