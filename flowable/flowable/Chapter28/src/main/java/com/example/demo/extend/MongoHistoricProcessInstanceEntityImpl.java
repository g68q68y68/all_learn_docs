package com.example.demo.extend;

import lombok.Data;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.persistence.entity.BpmnEngineEntityConstants;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.variable.service.impl.persistence.entity.HistoricVariableInstanceEntity;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "act_hi_procinst_mongo")
@Data
public class MongoHistoricProcessInstanceEntityImpl implements HistoricProcessInstanceEntity {
    private static final long serialVersionUID = 1L;

    @MongoId
    private String id;
    private int revision = 1;
    @Transient
    private boolean isInserted;
    @Transient
    private boolean isUpdated;
    @Transient
    private boolean isDeleted;
    @Transient
    private Object originalPersistentState;
    @Indexed
    private String processInstanceId;
    @Indexed
    private String processDefinitionId;
    private Date startTime;
    private Date endTime;
    private Long durationInMillis;
    private String deleteReason;
    private String endActivityId;
    private String businessKey;
    private String businessStatus;
    private String startUserId;
    private String startActivityId;
    private String superProcessInstanceId;
    private String tenantId = ProcessEngineConfiguration.NO_TENANT_ID;
    private String name;
    private String localizedName;
    private String description;
    private String localizedDescription;
    private String processDefinitionKey;
    private String processDefinitionName;
    private Integer processDefinitionVersion;
    private String deploymentId;
    private String callbackId;
    private String callbackType;
    private String referenceId;
    private String referenceType;
    @Transient
    private String propagatedStageInstanceId;
    @Transient
    private List<HistoricVariableInstanceEntity> queryVariables;

    public MongoHistoricProcessInstanceEntityImpl(){

    }

    public MongoHistoricProcessInstanceEntityImpl(ExecutionEntity processInstance){
        this.id = processInstance.getId();
        this.processInstanceId = processInstance.getId();
        this.businessKey = processInstance.getBusinessKey();
        this.businessStatus = processInstance.getBusinessStatus();
        this.name = processInstance.getName();
        this.processDefinitionId = processInstance.getProcessDefinitionId();
        this.processDefinitionKey = processInstance.getProcessDefinitionKey();
        this.processDefinitionName = processInstance.getProcessDefinitionName();
        this.processDefinitionVersion = processInstance.getProcessDefinitionVersion();
        this.deploymentId = processInstance.getDeploymentId();
        this.startActivityId = processInstance.getStartActivityId();
        this.startTime = processInstance.getStartTime();
        this.startUserId = processInstance.getStartUserId();
        this.superProcessInstanceId = processInstance.getSuperExecution() != null ? processInstance.getSuperExecution().getProcessInstanceId() : null;
        this.callbackId = processInstance.getCallbackId();
        this.callbackType = processInstance.getCallbackType();
        this.referenceId = processInstance.getReferenceId();
        this.referenceType = processInstance.getReferenceType();
        this.propagatedStageInstanceId = processInstance.getPropagatedStageInstanceId();

        // Inherit tenant id (if applicable)
        if (processInstance.getTenantId() != null) {
            this.tenantId = processInstance.getTenantId();
        }
    }

    @Override
    public int getRevisionNext() {
        return revision + 1;
    }

    @Override
    public void markEnded(String deleteReason, Date endTime) {
        if (this.endTime == null) {
            this.deleteReason = deleteReason;
            if (endTime != null) {
                this.endTime = endTime;
            } else {
                this.endTime = CommandContextUtil.getProcessEngineConfiguration().getClock().getCurrentTime();
            }
            if (endTime != null && startTime != null) {
                this.durationInMillis = endTime.getTime() - startTime.getTime();
            }
        }
    }

    @Override
    public String getIdPrefix() {
        return BpmnEngineEntityConstants.BPMN_ENGINE_ID_PREFIX;
    }

    @Override
    public Map<String, Object> getProcessVariables() {
        Map<String, Object> variables = new HashMap<>();
        if (queryVariables != null) {
            for (HistoricVariableInstanceEntity variableInstance : queryVariables) {
                if (variableInstance.getId() != null && variableInstance.getTaskId() == null) {
                    variables.put(variableInstance.getName(), variableInstance.getValue());
                }
            }
        }
        return variables;
    }

    @Override
    public Object getPersistentState() {
        Map<String, Object> persistentState = new HashMap<>();
        persistentState.put("startTime", startTime);
        persistentState.put("endTime", endTime);
        persistentState.put("businessKey", businessKey);
        persistentState.put("businessStatus", businessStatus);
        persistentState.put("name", name);
        persistentState.put("durationInMillis", durationInMillis);
        persistentState.put("deleteReason", deleteReason);
        persistentState.put("endActivityId", endActivityId);
        persistentState.put("superProcessInstanceId", superProcessInstanceId);
        persistentState.put("processDefinitionId", processDefinitionId);
        persistentState.put("processDefinitionKey", processDefinitionKey);
        persistentState.put("processDefinitionName", processDefinitionName);
        persistentState.put("processDefinitionVersion", processDefinitionVersion);
        persistentState.put("deploymentId", deploymentId);
        persistentState.put("callbackId", callbackId);
        persistentState.put("callbackType", callbackType);
        persistentState.put("referenceId", referenceId);
        persistentState.put("referenceType", referenceType);
        persistentState.put("propagatedStageInstanceId", propagatedStageInstanceId);
        return persistentState;
    }
}