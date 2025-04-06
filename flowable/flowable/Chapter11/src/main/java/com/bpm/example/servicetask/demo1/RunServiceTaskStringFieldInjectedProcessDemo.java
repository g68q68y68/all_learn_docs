package com.bpm.example.servicetask.demo1;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Test;

@Slf4j
public class RunServiceTaskStringFieldInjectedProcessDemo extends FlowableEngineUtil {
    @Test
    public void runServiceTaskStringFieldInjectedProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/ServiceTaskStringFieldInjectedProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("serviceTaskStringFieldInjectedProcess");
        //查询历史流程变量
        HistoricVariableInstance historicVariable = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(procInst.getId())
                .variableName("totalAmount")
                .singleResult();
        log.info("totalAmount的值为：{}", historicVariable.getValue());
    }
}