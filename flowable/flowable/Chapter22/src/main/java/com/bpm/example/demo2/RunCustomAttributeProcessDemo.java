package com.bpm.example.demo2;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

@Slf4j
public class RunCustomAttributeProcessDemo extends FlowableEngineUtil {
    @Test
    public void runCustomAttributeProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/CustomAttributeProcess.bpmn20.xml");
        //设置流程发起者
        identityService.setAuthenticatedUserId("liuxiaopeng");
        //发起流程实例
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("customAttributeProcess");
        log.info("流程标题为：{}", procInst.getName());
        identityService.setAuthenticatedUserId(null);
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询并办理第一个用户任务
        Task firstTask = taskQuery.singleResult();
        taskService.complete(firstTask.getId());
        //查询并办理第二个用户任务
        Task secondTask = taskQuery.singleResult();
        taskService.complete(secondTask.getId());
    }
}
