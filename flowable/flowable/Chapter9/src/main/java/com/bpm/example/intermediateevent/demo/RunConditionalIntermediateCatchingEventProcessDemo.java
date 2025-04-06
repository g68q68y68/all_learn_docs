package com.bpm.example.intermediateevent.demo;

import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.Map;

@Slf4j
public class RunConditionalIntermediateCatchingEventProcessDemo
        extends FlowableEngineUtil {
    @Test
    public void runConditionalIntermediateCatchingEventProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition procDef = deployResource("processes" +
                "/ConditionalIntermediateCatchingEventProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceById(procDef.getId());
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询并办理第一个用户任务
        Task firstTask = taskQuery.singleResult();
        log.info("流转到第1个Task：{}", firstTask.getName());
        taskService.complete(firstTask.getId());
        Task secondTask = null;
        int voteNum = 1;
        do {
            //设置变量并计算条件
            Map<String, Object> varMap1 = ImmutableMap.of("voteNum", voteNum);
            runtimeService.evaluateConditionalEvents(procInst.getId(), varMap1);
            //查询第二个用户任务
            secondTask = taskQuery.singleResult();
            if (secondTask == null) {
                log.info("voteNum=={}时没有触发条件中间捕获事件", voteNum);
                voteNum++;
            } else {
                log.info("voteNum=={}时触发条件中间捕获事件，流程流转到第2个Task：{}", voteNum,
                        secondTask.getName());
                break;
            }
        } while (true);
        //设置变量并计算条件
        Map<String, Object> varMap2 = ImmutableMap.of("materialState", false);
        runtimeService.evaluateConditionalEvents(procInst.getId(), varMap2);
        //查询并办理第三个用户任务
        Task thirdTask = taskQuery.singleResult();
        log.info("materialState==fasle时触发条件边界事件，流程流转到第3个Task：{}",
                thirdTask.getName());
        taskService.complete(thirdTask.getId());
        //查询并办理第四个用户任务
        Task fourthTask = taskQuery.singleResult();
        log.info("流程流转到第4个Task：{}", fourthTask.getName());
        taskService.complete(fourthTask.getId());
        //查询第五个用户任务
        Task fifthTask = taskQuery.singleResult();
        log.info("流程流转到第5个Task：{}", fifthTask.getName());
        //设置变量并计算条件
        Map<String, Object> varMap3 = ImmutableMap.of("result", "success");
        runtimeService.evaluateConditionalEvents(procInst.getId(), varMap3);
        //办理第5个任务
        taskService.complete(fifthTask.getId());
        //查询第六个用户任务
        Task sixTask = taskQuery.singleResult();
        log.info("流程流转到第6个Task：{}", sixTask.getName());
    }
}