package com.bpm.example.demo3;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo3.util.MultiInstanceExecutionUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.variable.api.persistence.entity.VariableInstance;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class RunAddAndDeleteMultiInstanceExecutionDemo extends FlowableEngineUtil {
    SimplePropertyPreFilter executionFilter = new SimplePropertyPreFilter(Execution.class,
            "id", "parentId", "businessKey", "processInstanceId", "scope", "activityId");
    SimplePropertyPreFilter variableFilter
            = new SimplePropertyPreFilter(VariableInstance.class,
            "name", "executionId", "value");

    @Test
    public void runAddAndDeleteMultiInstanceExecutionDemo() {
        //加载Flowalbe配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/MultiInstanceUserTaskProcess.bpmn20.xml");

        List<String> assigneeList1 = Lists.newArrayList("hebo", "litao");
        List<String> assigneeList2 = Lists.newArrayList("liushaoli", "xuqiangwei");
        Map<String, Object> varMap = ImmutableMap.of("assigneeList1", assigneeList1,
                "assigneeList2", assigneeList2);
        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("multiInstanceUserTaskProcess", varMap);
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询并完成第一个节点的任务
        Task firstTask = taskQuery.singleResult();
        //完成第一个节点的任务
        taskService.complete(firstTask.getId());
        log.info("用户任务{}办理完成", firstTask.getName());
        //查询第二个节点的任务
        List<Task> taskOfSecondNode = taskQuery.list();
        //输出执行实例和变量信息
        logExecutionAndVariableData(procInst.getId(), taskOfSecondNode.get(0).getName(),
                "执行加签前");
        MultiInstanceExecutionUtil multiInstanceExecutionUtil
                = new MultiInstanceExecutionUtil(repositoryService, runtimeService,
                taskService);
        //加签两个人
        multiInstanceExecutionUtil.addSign("assigneeList1", "assignee",
                procInst.getId(), taskOfSecondNode.get(0),
                Arrays.asList("wangjunlin", "huhaiqin"));
        //输出执行实例和变量信息
        logExecutionAndVariableData(procInst.getId(),
                taskOfSecondNode.get(0).getName(), "执行加签后");
        //执行减签一个人
        multiInstanceExecutionUtil.deleteSign("assigneeList1", procInst.getId(),
                taskOfSecondNode.get(0), "litao");
        //输出执行实例和变量信息
        logExecutionAndVariableData(procInst.getId(),
                taskOfSecondNode.get(0).getName(), "执行减签后");
        List<Task> taskList1 = taskQuery.list();
        //办理完成第二个节点的所有任务
        for (Task task : taskList1) {
            taskService.complete(task.getId());
            log.info("{}办理完成{}的用户任务", task.getAssignee(), task.getName());
        }
        //查询第三个节点的任务
        Task thirdTask = taskQuery.singleResult();
        //输出执行实例和变量信息
        logExecutionAndVariableData(procInst.getId(), thirdTask.getName(), "执行加签前");
        //执行加签两个人
        multiInstanceExecutionUtil.addSign("assigneeList2", "assignee",
                procInst.getId(), thirdTask, Arrays.asList("wangxiaolong", "wanglina"));
        //输出执行实例和变量信息
        logExecutionAndVariableData(procInst.getId(), thirdTask.getName(), "执行加签后");
        //执行减签一个人
        multiInstanceExecutionUtil.deleteSign("assigneeList2", procInst.getId(),
                thirdTask, "xuqiangwei");
        //输出执行实例和变量信息
        logExecutionAndVariableData(procInst.getId(), thirdTask.getName(), "执行减签后");
        //办理完成第三个节点的所有任务
        for (int i = 0; i < 3; i++) {
            Task task = taskQuery.singleResult();
            taskService.complete(task.getId());
            log.info("{}办理完成用户任务：{}", task.getAssignee(), task.getName());
        }
        //查询第四个节点的任务
        Task forthTask = taskQuery.singleResult();
        log.info("流程流转到：{}", forthTask.getName());
    }

    /**
     * 输出执行实例和变量信息
     *
     * @param processInstanceId 流程实例编号
     * @param taskName          任务名称
     * @param operate           操作名称
     */
    private void logExecutionAndVariableData(String processInstanceId,
                                             String taskName, String operate) {
        List<Execution> executionList = runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId).list();
        log.info("{}节点{}的执行实例数为{}，分别是：{}", taskName, operate,
                executionList.size(),
                JSON.toJSONString(executionList, executionFilter));
        List<VariableInstance> variables = runtimeService.createVariableInstanceQuery()
                .processInstanceId(processInstanceId).list();
        log.info("{}节点{}的流程变量为：{}", taskName, operate,
                JSON.toJSONString(variables, variableFilter));
    }
}
