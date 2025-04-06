package com.bpm.example.servicetask.demo1;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.Test;

@Slf4j
public class RunFutureJavaDelegateServiceTaskProcessDemo extends FlowableEngineUtil {
    @Test
    public void runFutureJavaDelegateServiceTaskProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition processDefinition = deployResource("processes/FutureJavaDelegateServiceTaskProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceById(processDefinition.getId());
    }
}