package com.bpm.example.demo3;

import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RunTaskSkipDemo extends FlowableEngineUtil {
    @Test
    public void runTaskSkipDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/AutoSkipProcess.bpmn20.xml");

        //设置流程变量
        Map<String, Object> varMap = ImmutableMap.of("employee", "liuxiaopeng",
                "leader", "hebo", "manager", "hebo", "hr", "huhaiqin",
                "_FLOWABLE_SKIP_EXPRESSION_ENABLED", true);
        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("autoSkipProcess", varMap);
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询并完成第一个任务
        Task firstTask = taskQuery.singleResult();
        taskService.complete(firstTask.getId());
        log.info("用户任务{}办理完成。", firstTask.getName());
        //查询并完成第二个任务
        Task secondTask = taskQuery.singleResult();
        taskService.complete(secondTask.getId());
        log.info("用户任务{}办理完成。", secondTask.getName());
        //查询第三个任务
        Task thirdTask = taskQuery.singleResult();
        log.info("当前流程所处节点名称为：{}，节点key为：{}", thirdTask.getName(),
                thirdTask.getTaskDefinitionKey());
    }
}
