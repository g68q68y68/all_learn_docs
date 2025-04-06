package com.bpm.example.demo2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo2.service.TaskRecallService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ExecutionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

@Slf4j
public class RunTaskRecallDemo extends FlowableEngineUtil {
    SimplePropertyPreFilter taskFilter = new SimplePropertyPreFilter(Task.class,
            "id", "name", "executionId", "taskDefinitionKey");
    SimplePropertyPreFilter executionFilter = new SimplePropertyPreFilter(Execution.class,
            "id", "parentId", "processInstanceId", "activityId", "isScope");
    SimplePropertyPreFilter historicActivityFilter
            = new SimplePropertyPreFilter(HistoricActivityInstance.class,
            "id", "activityId", "activityName", "taskId");

    @Test
    public void runTaskRecallDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/TaskRecallProcess.bpmn20.xml");

        //发起流程实例
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("taskRecallProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        ExecutionQuery executionQuery = runtimeService.createExecutionQuery()
                .processInstanceId(procInst.getId());
        HistoricActivityInstanceQuery hisActivityQuery = historyService
                .createHistoricActivityInstanceQuery()
                .processInstanceId(procInst.getId());
        //查询借款申请用户任务的task
        Task firstTask = taskQuery.singleResult();
        //完成借款申请用户任务的task
        taskService.complete(firstTask.getId());
        log.info("撤回前，当前任务为：{}", JSON.toJSONString(taskQuery.list(), taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));
        log.info("历史节点实例有：{}", JSON.toJSONString(hisActivityQuery.list(),
                historicActivityFilter));

        //执行撤回操作
        TaskRecallService taskRecallService = new TaskRecallService(managementService);
        taskRecallService.executeRecall(firstTask.getId());

        log.info("撤回后，当前任务为：{}", JSON.toJSONString(taskQuery.list(), taskFilter));
        log.info("当前执行实例有：{}", JSON.toJSONString(executionQuery.list(), executionFilter));
        log.info("历史节点实例有：{}", JSON.toJSONString(hisActivityQuery.list(),
                historicActivityFilter));
    }
}
