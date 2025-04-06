package com.bpm.example.demo5.mapper;

import org.apache.ibatis.annotations.Select;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;

import java.util.List;
import java.util.Map;

public interface CustomSqlMapper {

    //使用@Select注解定义根据businessKey查询ProcessInstance的自定义SQL
    @Select({"select ID_,PROC_INST_ID_,BUSINESS_KEY_,PROC_DEF_ID_,NAME_,START_TIME_," +
            "START_USER_ID_ from ACT_RU_EXECUTION where BUSINESS_KEY_ = #{businessKey}"})
    Map<String, String> customSelectProcessInstanceByBusinessKey(String businessKey);

    //使用@Select注解定义根据processInstanceId查询Task的自定义SQL
    @Select({"select ID_ as id, REV_ as revision, NAME_ as name, PARENT_TASK_ID_ as " +
            "parentTaskId, DESCRIPTION_ as description, PRIORITY_ as priority, CREATE_TIME_ " +
            "as createTime, OWNER_ as owner, ASSIGNEE_ as assignee, DELEGATION_ as " +
            "delegationStateString, EXECUTION_ID_ as executionId, PROC_INST_ID_ as " +
            "processInstanceId, PROC_DEF_ID_ as processDefinitionId, TASK_DEF_KEY_ as " +
            "taskDefinitionKey, DUE_DATE_ as dueDate, CATEGORY_ as category, " +
            "SUSPENSION_STATE_ as suspensionState, TENANT_ID_ as tenantId, FORM_KEY_ as " +
            "formKey, CLAIM_TIME_ as claimTime from ACT_RU_TASK where PROC_INST_ID_ = " +
            "#{processInstanceId}"})
    List<TaskEntityImpl> customSelectTasksByProcessId(String processInstanceId);

    //使用@Select注解定义根据taskId查询IdentityLink的自定义SQL
    @Select({"select t1.NAME_ as PROC_INST_NAME_, t2.NAME_ as TASK_NAME_, t1.BUSINESS_KEY_, " +
            "t3.TYPE_, t3.USER_ID_ from ACT_RU_EXECUTION t1 join ACT_RU_TASK t2 on t1.ID_ = " +
            "t2.PROC_INST_ID_ join ACT_RU_IDENTITYLINK t3 on t3.TASK_ID_ = t2.ID_ where " +
            "t3.TASK_ID_ = #{taskId}"})
    List<Map<String, String>> customSelectIdentityLinkByTaskId(String taskId);
}
