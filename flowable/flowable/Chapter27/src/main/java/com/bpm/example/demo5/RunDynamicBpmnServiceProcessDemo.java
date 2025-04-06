package com.bpm.example.demo5;

import com.bpm.common.util.FlowableEngineUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

@Slf4j
public class RunDynamicBpmnServiceProcessDemo extends FlowableEngineUtil {
    @Test
    public void runDynamicBpmnServiceDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/DynamicBpmnServiceProcess.bpmn20.xml");

        //启动修改前的流程
        ProcessInstance procInst1 = runtimeService
                .startProcessInstanceByKey("dynamicBpmnServiceProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst1.getId());
        Task firstTask = taskQuery.singleResult();
        log.info("修改前第一个节点的名称：{}", firstTask.getName());
        taskService.complete(firstTask.getId());
        Task secondTask = taskQuery.singleResult();
        log.info("第二个节点的名称：{}，办理人：{}", secondTask.getName(), secondTask.getAssignee());

        //查询ProcessDefinitionInfoCacheObject的infoNode值
        ObjectNode infoNode = dynamicBpmnService
                .getProcessDefinitionInfo(procInst1.getProcessDefinitionId());
        //修改第一个节点的名称
        dynamicBpmnService.changeUserTaskName("application", "请假申请", infoNode);
        //修改第二个节点的名称和办理人
        dynamicBpmnService.changeUserTaskName("approval", "上级审批", infoNode);
        dynamicBpmnService.changeUserTaskAssignee("approval", "hebo", infoNode);
        //保存动态修改后的ProcessDefinitionInfoCacheObject
        dynamicBpmnService.saveProcessDefinitionInfo(procInst1
                .getProcessDefinitionId(), infoNode);

        //启动修改后的流程
        ProcessInstance procInst2 = runtimeService
                .startProcessInstanceByKey("dynamicBpmnServiceProcess");
        taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst2.getId());
        firstTask = taskQuery.singleResult();
        log.info("修改后第一个节点的名称：{}", firstTask.getName());
        taskService.complete(firstTask.getId());
        secondTask = taskQuery.singleResult();
        log.info("第二个节点的名称：{}，办理人：{}", secondTask.getName(),
                secondTask.getAssignee());
    }
}
