package com.bpm.example.demo3;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

@Slf4j
public class RunNativeSqlProcessDemo extends FlowableEngineUtil {
    SimplePropertyPreFilter processFilter = new SimplePropertyPreFilter(ProcessInstance.class,
            "id", "revision", "scope", "parentId", "businessKey", "processInstanceId",
            "activityId", "processDefinitionKey", "processDefinitionId",
            "rootProcessInstanceId", "startTime");
    SimplePropertyPreFilter taskFilter = new SimplePropertyPreFilter(Task.class,
            "id", "revision", "name", "parentTaskId", "description", "priority",
            "createTime", "owner", "assignee", "delegationStateString", "executionId",
            "claimTime", "taskDefinitionKey", "processInstanceId", "processDefinitionId",
            "dueDate", "category", "tenantId", "formKey", "suspensionState");

    @Test
    public void runNativeSqlProcessDemo() throws Exception {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/NativeSqlProcess.bpmn20.xml");

        //启动流程
        String businessKey = UUID.randomUUID().toString();
        runtimeService.startProcessInstanceByKey("nativeSqlProcess", businessKey);

        //通过NativeSql查询单个ProcessInstance对象
        ProcessInstance processInstance = runtimeService
                .createNativeProcessInstanceQuery()
                .sql("select * from ACT_RU_EXECUTION where BUSINESS_KEY_ = #{businessKey}")
                .parameter("businessKey", businessKey)
                .singleResult();
        log.info("流程实例信息为：{}", JSON.toJSONString(processInstance, processFilter));

        //通过NativeSql查询任务列表
        List<Task> tasks = taskService.createNativeTaskQuery()
                .sql("select ID_, REV_ , NAME_, PARENT_TASK_ID_, DESCRIPTION_, PRIORITY_,"
                        + " CREATE_TIME_, OWNER_, ASSIGNEE_, DELEGATION_, EXECUTION_ID_,"
                        + " PROC_INST_ID_, PROC_DEF_ID_, TASK_DEF_KEY_, DUE_DATE_, CATEGORY_,"
                        + " SUSPENSION_STATE_, TENANT_ID_, FORM_KEY_, CLAIM_TIME_"
                        + " from ACT_RU_TASK where PROC_INST_ID_ = #{processInstanceId}")
                .parameter("processInstanceId", processInstance.getId())
                .list();
        log.info("流程中的待办任务有：{}", JSON.toJSONString(tasks, taskFilter));

        //通过NativeSql查询任务个数
        long num = taskService.createNativeTaskQuery()
                .sql("select count(0) from ACT_RU_TASK as t1"
                        + " join ACT_RU_IDENTITYLINK as t2 on t1.ID_ = t2.TASK_ID_"
                        + " where t1.ASSIGNEE_ is null and t2.USER_ID_ = #{userId}")
                .parameter("userId", "huhaiqin")
                .count();
        log.info("用户huhaiqin的待办任务数为：{}", num);
    }
}