package com.bpm.example.demo4;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo4.event.CustomFlowableEventType;
import com.bpm.example.demo4.event.impl.FlowableTaskUrgingEventImpl;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.impl.event.FlowableEventSupport;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.junit.Test;

@Slf4j
public class RunDemo4 extends FlowableEngineUtil {
    @Test
    public void runCustomEventDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/CustomEventProcess.bpmn20.xml");

        //启动流程实例
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("customEventProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询并完成第一个用户任务
        Task firstTask = taskQuery.singleResult();
        taskService.complete(firstTask.getId());
        //查询第二个用户任务
        Task secondTask = taskQuery.singleResult();

        //查询BpmnModel
        BpmnModel bpmnModel = repositoryService
                .getBpmnModel(procInst.getProcessDefinitionId());
        //获取FlowableEventSupport
        FlowableEventSupport flowableEventSupport
                = ((FlowableEventSupport) bpmnModel.getEventSupport());
        //触发自定义事件
        flowableEventSupport.dispatchEvent(new FlowableTaskUrgingEventImpl((TaskEntity)
                secondTask, FlowableEngineEventType.CUSTOM,
                CustomFlowableEventType.TASK_URGING));
    }
}
