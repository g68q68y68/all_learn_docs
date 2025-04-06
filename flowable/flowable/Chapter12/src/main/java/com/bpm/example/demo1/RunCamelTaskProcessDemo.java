package com.bpm.example.demo1;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;

@Slf4j
public class RunCamelTaskProcessDemo {
    @Test
    public void runCamelTaskProcessDemo() {
        ApplicationContext applicationContext
                = new ClassPathXmlApplicationContext("flowable.camel.xml");
        //创建流程引擎配置
        ProcessEngineConfiguration processEngineConfiguration = applicationContext
                .getBean("processEngineConfiguration", ProcessEngineConfiguration.class);
        //创建流程引擎
        ProcessEngine engine = processEngineConfiguration.buildProcessEngine();
        //部署流程
        engine.getRepositoryService().createDeployment()
                .addClasspathResource("processes/CamelTaskProcess.bpmn20.xml")
                .deploy();
        //启动流程
        ProcessInstance procInst = engine.getRuntimeService()
                .startProcessInstanceByKey("camelTaskProcess");
        //查询第一个任务
        Task task = engine.getTaskService().createTaskQuery()
                .processInstanceId(procInst.getId()).singleResult();
        //设置流程变量
        Map<String, Object> varMap = ImmutableMap.of("ip", "119.99.130.0");
        //办理第一个任务
        engine.getTaskService().complete(task.getId(), varMap);
        //查询并打印流程变量
        List<HistoricVariableInstance> hisVars = engine.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(procInst.getId()).list();
        hisVars.stream().forEach((hisVar) -> log.info("流程变量名：{}，变量值：{}",
                hisVar.getVariableName(), JSON.toJSONString(hisVar.getValue())));

        //关闭流程引擎
        engine.close();
    }
}