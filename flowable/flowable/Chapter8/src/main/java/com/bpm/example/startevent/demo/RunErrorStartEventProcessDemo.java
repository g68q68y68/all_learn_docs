package com.bpm.example.startevent.demo;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.Test;

@Slf4j
public class RunErrorStartEventProcessDemo extends FlowableEngineUtil {
    @Test
    public void runErrorStartEventProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/ErrorStartEventProcess.bpmn20.xml");

        //发起流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("errorStartEventProcess");
        log.info("流程实例id：{}", procInst.getId());
    }
}