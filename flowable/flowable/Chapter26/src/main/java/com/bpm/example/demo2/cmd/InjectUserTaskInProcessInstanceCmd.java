package com.bpm.example.demo2.cmd;

import lombok.AllArgsConstructor;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.util.io.BytesStreamSource;
import org.flowable.engine.impl.cmd.AbstractDynamicInjectionCmd;
import org.flowable.engine.impl.dynamic.BaseDynamicSubProcessInjectUtil;
import org.flowable.engine.impl.dynamic.DynamicUserTaskBuilder;
import org.flowable.engine.impl.persistence.entity.DeploymentEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.flowable.engine.impl.persistence.entity.ResourceEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.util.List;

@AllArgsConstructor
public class InjectUserTaskInProcessInstanceCmd
        extends AbstractDynamicInjectionCmd implements Command<Void> {
    protected String taskId;
    protected DynamicUserTaskBuilder dynamicUserTaskBuilder;

    @Override
    public Void execute(CommandContext commandContext) {
        Task task = CommandContextUtil.getProcessEngineConfiguration(commandContext)
                .getTaskServiceConfiguration().getTaskEntityManager().findById(taskId);
        createDerivedProcessDefinitionForProcessInstance(commandContext,
                task.getProcessInstanceId(), task.getTaskDefinitionKey());
        return null;
    }

    /**
     * 根据当前流程实例编号和节点编号创建新的流程定义
     */
    protected void createDerivedProcessDefinitionForProcessInstance(
            CommandContext commandContext, String processInstanceId, String currentTaskKey) {
        //获取当前流程实例
        ProcessInstance procInst = CommandContextUtil
                .getExecutionEntityManager().findById(processInstanceId);
        //获取流程模型
        BpmnModel bpmnModel = ProcessDefinitionUtil
                .getBpmnModel(procInst.getProcessDefinitionId());
        //获取流程当前所在节点
        FlowElement currentFlowElement = bpmnModel.getFlowElement(currentTaskKey);
        createDerivedProcessDefinition(commandContext, procInst, currentFlowElement);
    }

    /**
     * 根据流程实例创建新的流程定义
     */
    protected void createDerivedProcessDefinition(CommandContext commandContext,
                                                  ProcessInstance procInst,
                                                  FlowElement currentFlowElement) {
        //获取流程定义
        ProcessDefinitionEntity originalProcessDefinitionEntity = CommandContextUtil
                .getProcessDefinitionEntityManager()
                .findById(procInst.getProcessDefinitionId());
        //创建修改后的部署对象
        DeploymentEntity deploymentEntity = createDerivedDeployment(commandContext,
                originalProcessDefinitionEntity);
        //创建新的流程模型
        BpmnModel bpmnModel = createBpmnModel(commandContext,
                originalProcessDefinitionEntity,
                deploymentEntity, currentFlowElement);
        //保存新版流程模型的资源
        storeBpmnModelAsByteArray(commandContext, bpmnModel, deploymentEntity,
                originalProcessDefinitionEntity.getResourceName());
        //动态部署修改后的流程定义
        ProcessDefinitionEntity derivedProcessDefinitionEntity
                = deployDerivedDeploymentEntity(commandContext,
                deploymentEntity, originalProcessDefinitionEntity);
        //将流程实例的所有运行时和历史数据更换至新的流程定义
        updateExecutions(commandContext, derivedProcessDefinitionEntity,
                (ExecutionEntity) procInst, bpmnModel);
    }

    /**
     * 根据旧的流程定义创建新的流程模型
     */
    protected BpmnModel createBpmnModel(CommandContext commandContext,
                                        ProcessDefinitionEntity originalProcessDefinition,
                                        DeploymentEntity newDeploymentEntity,
                                        FlowElement currentFlowElement) {
        //获取旧流程的部署资源
        ResourceEntity originalBpmnResource = CommandContextUtil
                .getResourceEntityManager()
                .findResourceByDeploymentIdAndResourceName(originalProcessDefinition
                        .getDeploymentId(), originalProcessDefinition.getResourceName());
        //获取旧流程的流程模型
        BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(
                new BytesStreamSource(originalBpmnResource.getBytes()),
                false, false);
        Process process = bpmnModel.getProcessById(originalProcessDefinition.getKey());
        //动态修改流程模型
        updateBpmnProcess(commandContext, process, currentFlowElement, bpmnModel,
                originalProcessDefinition, newDeploymentEntity);
        return bpmnModel;
    }

    /**
     * 动态修改流程模型
     */
    protected void updateBpmnProcess(CommandContext commandContext, Process process,
                                     FlowElement currentFlowElement, BpmnModel bpmnModel,
                                     ProcessDefinitionEntity originalProcessDefinitionEntity,
                                     DeploymentEntity newDeploymentEntity) {
        //新建节点
        UserTask userTask = new UserTask();
        if (dynamicUserTaskBuilder.getId() != null) {
            userTask.setId(dynamicUserTaskBuilder.getId());
        } else {
            userTask.setId(dynamicUserTaskBuilder
                    .nextTaskId(process.getFlowElementMap()));
        }
        dynamicUserTaskBuilder.setDynamicTaskId(userTask.getId());
        userTask.setName(dynamicUserTaskBuilder.getName());
        userTask.setAssignee(dynamicUserTaskBuilder.getAssignee());
        process.addFlowElement(userTask);
        //找到当前节点的出口
        List<SequenceFlow> outgoingFlows = ((FlowNode) currentFlowElement)
                .getOutgoingFlows();
        //设置新增节点的出口
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            process.removeFlowElement(outgoingFlow.getId());
            outgoingFlow.setSourceRef(userTask.getId());
            process.addFlowElement(outgoingFlow);
        }
        //创建当前节点与新建节点的连线
        SequenceFlow flowToUserTask = new SequenceFlow(currentFlowElement.getId(),
                userTask.getId());
        flowToUserTask.setId(dynamicUserTaskBuilder
                .nextFlowId(process.getFlowElementMap()));
        process.addFlowElement(flowToUserTask);
        //自动布局
        new BpmnAutoLayout(bpmnModel).execute();
        BaseDynamicSubProcessInjectUtil.processFlowElements(commandContext, process,
                bpmnModel, originalProcessDefinitionEntity, newDeploymentEntity);
    }

    @Override
    protected void updateBpmnProcess(CommandContext commandContext, Process process,
                                     BpmnModel bpmnModel,
                                     ProcessDefinitionEntity originalProcessDefinitionEntity,
                                     DeploymentEntity newDeploymentEntity) {

    }

    @Override
    protected void updateExecutions(CommandContext commandContext,
                                    ProcessDefinitionEntity processDefinitionEntity,
                                    ExecutionEntity processInstance,
                                    List<ExecutionEntity> childExecutions) {

    }
}
