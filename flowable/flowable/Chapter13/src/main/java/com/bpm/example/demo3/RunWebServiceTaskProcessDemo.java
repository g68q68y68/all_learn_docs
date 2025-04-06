package com.bpm.example.demo3;

import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class RunWebServiceTaskProcessDemo extends FlowableEngineUtil {
    @Test
    public void runWebServiceTaskProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.mail.xml");
        //部署流程
        deployResource("processes/WebServiceTaskProcess.bpmn20.xml");

        //设置流程变量
        Map<String, Object> varMap = ImmutableMap.of("userName", "诗雨花魂",
                "userMail", "280106637@qq.com", "myMobileCode", "13693511055");
        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("webServiceTaskProcess", varMap);
        //查询并打印流程变量
        List<HistoricVariableInstance> hisVariables = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(procInst.getId()).list();
        hisVariables.stream().forEach(hisVariable -> log.info("流程变量名：{}，变量值：{}",
                hisVariable.getVariableName(), hisVariable.getValue()));
    }
}