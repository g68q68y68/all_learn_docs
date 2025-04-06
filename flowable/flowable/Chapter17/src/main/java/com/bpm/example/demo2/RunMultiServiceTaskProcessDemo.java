package com.bpm.example.demo2;

import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Test;

import java.util.List;
import java.util.Map;

@Slf4j
public class RunMultiServiceTaskProcessDemo extends FlowableEngineUtil {
    @Test
    public void runMultiServiceTaskProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/MultiServiceTaskProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("multiServiceTaskProcess");
        //查询第一个任务
        Task userTask1 = taskService.createTaskQuery()
                .processInstanceId(procInst.getId()).singleResult();
        log.info("即将完成第一个任务，当前任务名称：{}", userTask1.getName());
        //设置流程变量
        List<String> userIds = Lists.newArrayList("hebo", "liuxiaopeng", "huhaiqin");
        Map<String, Object> varMap = ImmutableMap.of("userIdList", userIds);
        //完成第一个任务
        taskService.complete(userTask1.getId(), varMap);
    }
}
