package com.bpm.example.demo1.dynamic;

import lombok.AllArgsConstructor;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;

@AllArgsConstructor
public class DynamicProcessCreateUtil {
    private RepositoryService repositoryService;
    private BpmnModel model;
    private Process process;

    /**
     * 创建流程
     * @param processKey  流程key
     * @param processName 流程名称
     */
    public void createProcess(String processKey, String processName) {
        this.model.addProcess(process);
        this.process.setId(processKey);
        this.process.setName(processName);
    }

    /**
     * 创建开始节点
     * @param id     节点编号
     * @param name   节点名称
     * @return
     */
    public StartEvent createStartEvent(String id, String name) {
        StartEvent startEvent = new StartEvent();
        startEvent.setId(id);
        startEvent.setName(name);
        this.process.addFlowElement(startEvent);
        return startEvent;
    }

    /**
     * 创建结束节点
     * @param id     节点编号
     * @param name   节点名称
     * @return
     */
    public EndEvent createEndEvent(String id, String name) {
        EndEvent endEvent = new EndEvent();
        endEvent.setId(id);
        endEvent.setName(name);
        this.process.addFlowElement(endEvent);
        return endEvent;
    }

    /**
     * 创建用户任务
     * @param id         节点编号
     * @param name       节点名称
     * @param assignee   办理人
     * @return
     */
    public UserTask createUserTask(String id, String name, String assignee) {
        UserTask userTask = new UserTask();
        userTask.setName(name);
        userTask.setId(id);
        userTask.setAssignee(assignee);
        this.process.addFlowElement(userTask);
        return userTask;
    }

    /**
     * 创建排它网关
     * @param id    节点编号
     * @param name  节点名称
     * @return
     */
    public ExclusiveGateway createExclusiveGateway(String id, String name) {
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setId(id);
        exclusiveGateway.setName(name);
        this.process.addFlowElement(exclusiveGateway);
        return exclusiveGateway;
    }

    /**
     * 创建顺序流
     * @param from  起始节点编号
     * @param to    目标节点编号
     * @return
     */
    public SequenceFlow createSequenceFlow(String from, String to) {
        SequenceFlow flow = new SequenceFlow();
        flow.setSourceRef(from);
        flow.setTargetRef(to);
        this.process.addFlowElement(flow);
        return flow;
    }

    /**
     * 创建带条件的顺序流
     * @param from                    起始节点编号
     * @param to                      目标节点编号
     * @param conditionExpression   条件
     * @return
     */
    public SequenceFlow createSequenceFlow(String from, String to, String conditionExpression) {
        SequenceFlow flow = new SequenceFlow();
        flow.setSourceRef(from);
        flow.setTargetRef(to);
        flow.setConditionExpression(conditionExpression);
        this.process.addFlowElement(flow);
        return flow;
    }

    /**
     * 生成BPMN自动布局
     */
    public void autoLayout() {
        new BpmnAutoLayout(model).execute();
    }

    /**
     * 部署流程
     * @return
     */
    public ProcessDefinition deployProcess() {
        Deployment deployment = repositoryService.createDeployment()
                .addBpmnModel(process.getId() + ".bpmn", model)
                .name(process.getName()).deploy();
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        return processDefinition;
    }
}