package com.bpm.example.demo3;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo3.service.RevokeProcessInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.List;

@Slf4j
public class RunRevokeProcessInstanceDemo extends FlowableEngineUtil {
    SimplePropertyPreFilter taskFilter = new SimplePropertyPreFilter(Task.class, "id",
            "name", "processInstanceId", "taskDefinitionKey");

    @Test
    public void runRevokeProcessInstanceDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/RevokeProcessInstanceProcess.bpmn20.xml");

        //设置流程发起人
        Authentication.setAuthenticatedUserId("huhaiqin");
        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("revokeProcessInstanceProcess");
        Authentication.setAuthenticatedUserId(null);
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询特殊借款申请用户任务的task
        Task firstTask = taskQuery.singleResult();
        //完成特殊借款申请用户任务的task
        taskService.complete(firstTask.getId());
        log.info("完成用户任务：{}", firstTask.getName());

        List<Task> taskList1 = taskQuery.list();
        taskList1.stream().forEach(task -> {
            log.info("完成用户任务：{}", task.getName());
            taskService.complete(task.getId());
        });
        log.info("流程撤销前，当前任务为：{}", JSON.toJSONString(taskQuery.list(), taskFilter));
        Authentication.setAuthenticatedUserId("huhaiqin");
        //执行流程撤销操作
        RevokeProcessInstanceService revokeProcessService
                = new RevokeProcessInstanceService(managementService);
        revokeProcessService.executeRevoke(procInst.getId());
        log.info("流程撤销后，当前任务为：{}", JSON.toJSONString(taskQuery.list(), taskFilter));
    }
}