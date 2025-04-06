package com.bpm.example.demo1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo1.service.RestartProcessInstanceService;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class RunRestartProcessInstanceDemo extends FlowableEngineUtil {
    SimplePropertyPreFilter taskFilter = new SimplePropertyPreFilter(Task.class,
            "id", "name", "executionId", "taskDefinitionKey");
    SimplePropertyPreFilter executionFilter = new SimplePropertyPreFilter(Execution.class,
            "id", "parentId", "processInstanceId", "activityId", "isScope");

    @Test
    public void runRestartProcessInstanceDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/RestartProcessInstanceProcess.bpmn20.xml");
        //设置流程变量
        Map<String, Object> varMap = ImmutableMap.of("applyUserId", "hebo",
                "applyNum", 100, "totalAmount", 9999.99);
        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("restartProcessInstanceProcess", varMap);
        //自动办理任务至流程结束
        autoCompleteTasks(procInst.getId());
        //流程复活
        RestartProcessInstanceService restartProcessInstanceService
                = new RestartProcessInstanceService(managementService);
        restartProcessInstanceService.executeRestart(procInst.getId(),
                Arrays.asList("userTask2", "userTask3", "userTask4"));
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(procInst.getId()).list();
        log.info("复活后，当前任务为：{}", JSON.toJSONString(tasks, taskFilter));
        List<Execution> executions = runtimeService.createExecutionQuery()
                .processInstanceId(procInst.getId()).list();
        log.info("当前执行实例有：{}", JSON.toJSONString(executions, executionFilter));
        Map<String, Object> newVarMap = runtimeService.getVariables(procInst.getId());
        log.info("运行时流程变量有：{}", JSON.toJSONString(newVarMap));
    }

    //自动办理任务至流程结束
    private void autoCompleteTasks(String processInstanceId) {
        boolean isProcessInstanceEnded = false;
        while (!isProcessInstanceEnded) {
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(processInstanceId).list();
            if (!CollectionUtils.isEmpty(tasks)) {
                for (Task task : tasks) {
                    taskService.complete(task.getId());
                }
            }
            HistoricProcessInstance historicProcessInstance = historyService
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();
            if (historicProcessInstance.getEndTime() != null) {
                isProcessInstanceEnded = true;
            }
        }
    }
}
