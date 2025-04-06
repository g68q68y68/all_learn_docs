package com.bpm.example.demo2;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo2.service.InjectUserTaskInProcessInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
public class RunInjectUserTaskInProcessInstanceDemo extends FlowableEngineUtil {
    @Test
    public void runInjectUserTaskInProcessInstanceDemo() throws Exception {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/InjectUserTaskProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("injectUserTaskProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询第一个任务
        Task firstTask = taskQuery.singleResult();
        //完成第一个任务
        taskService.complete(firstTask.getId());
        //查询第二个任务
        Task secondTask = taskQuery.singleResult();
        log.info("当前流程的流程定义id为：{}", procInst.getProcessDefinitionId());
        //查询流程模型
        BpmnModel bpmnModel = repositoryService
                .getBpmnModel(procInst.getProcessDefinitionId());
        log.info("当前流程的用户任务数为：{}", bpmnModel.getMainProcess()
                .findFlowElementsOfType(UserTask.class).size());
        //导出动态加节点前的流程图
        exportProcessDiagram(procInst.getProcessDefinitionId(), "加节点前图片");
        //动态增加节点
        InjectUserTaskInProcessInstanceService injectUserTaskInProcessInstanceService
                = new InjectUserTaskInProcessInstanceService(managementService);
        injectUserTaskInProcessInstanceService.executeInjectUserTaskInProcessInstance(
                secondTask.getId(), "userTask4", "部门经理审批", "zhangsan");
        //重新查询流程实例
        procInst = runtimeService.createProcessInstanceQuery()
                .processInstanceId(procInst.getId()).singleResult();
        log.info("加节点后流程的流程定义id为：{}", procInst.getProcessDefinitionId());
        bpmnModel = repositoryService.getBpmnModel(procInst.getProcessDefinitionId());
        log.info("加节点后流程的用户任务数为：{}", bpmnModel.getMainProcess()
                .findFlowElementsOfType(UserTask.class).size());
        //导出动态加节点后的流程图和流程定义xml
        exportProcessDiagram(procInst.getProcessDefinitionId(), "加节点后图片");
        exportProcessDefinitionXml(procInst.getProcessDefinitionId());
        //依次办理流程中的各个任务
        while (true) {
            List<Task> tasks = taskQuery.list();
            if (CollectionUtils.isEmpty(tasks)) {
                break;
            }
            for (Task task : tasks) {
                log.info("办理用户任务：{}，节点key为：{}", task.getName(),
                        task.getTaskDefinitionKey());
                taskService.complete(task.getId());
            }
        }
        //查询历史流程实例
        HistoricProcessInstance hisProcInst = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(procInst.getId()).singleResult();
        if (hisProcInst.getEndTime() != null) {
            log.info("流程已结束");
        }
    }
}
