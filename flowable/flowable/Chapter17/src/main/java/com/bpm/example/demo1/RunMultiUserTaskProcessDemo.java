package com.bpm.example.demo1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ExecutionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.variable.api.persistence.entity.VariableInstance;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class RunMultiUserTaskProcessDemo extends FlowableEngineUtil {
    SimplePropertyPreFilter executionFilter = new SimplePropertyPreFilter(Execution.class,
            "id", "parentId", "businessKey", "processInstanceId", "scope", "activityId");

    @Test
    public void runMultiUserTaskProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/MultiUserTaskProcess.bpmn20.xml");

        //设置流程变量
        List<String> assigneeList = Lists.newArrayList("litao", "huhaiqin",
                "wangjunlin", "liuxiaopeng", "hebo");
        Map<String, Object> varMap1 = ImmutableMap.of("assigneeList", assigneeList);
        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("multiUserTaskProcess", varMap1);
        ExecutionQuery executionQuery = runtimeService.createExecutionQuery()
                .processInstanceId(procInst.getId());
        //查询执行实例
        List<Execution> executionList1 = executionQuery.list();
        log.info("流程发起后，执行实例数为：{}，分别为：{}", executionList1.size(),
                JSON.toJSONString(executionList1, executionFilter));
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询第一个任务
        Task firstTask = taskQuery.singleResult();
        log.info("即将完成第一个节点的任务，当前任务taskId：{}，taskName：{}",
                firstTask.getId(), firstTask.getName());
        //完成第一个任务
        taskService.complete(firstTask.getId());
        //串行多实例用户任务，需要逐个依次办理，办理五次
        for (int i = 0; i < assigneeList.size(); i++) {
            log.info("即将办理串行多实例第{}个任务", (i + 1));
            List<Task> tasks = taskQuery.list();
            log.info("此时任务个数为：{}", tasks.size());
            log.info("当前任务taskId：{}，taskName：{}，办理人：{}", tasks.get(0).getId(),
                    tasks.get(0).getName(), tasks.get(0).getAssignee());
            //查询执行实例
            List<Execution> executionList2 = executionQuery.list();
            log.info("到达串行多实例节点后，执行实例数为：{}，分别为：{}", executionList2.size(),
                    JSON.toJSONString(executionList2, executionFilter));
            //查询流程变量
            List<Map<String, Object>> varMap2 = getAllVariables(procInst.getId());
            log.info("当前流程变量为：{}", JSON.toJSONString(varMap2));
            taskService.complete(tasks.get(0).getId());
        }

        //并行多实例用户任务，5*0.6=3个办完之后结束
        for (int i = 0; i < 3; i++) {
            log.info("办理并行多实例第{}个任务", (i + 1));
            List<Task> tasks = taskQuery.list();
            log.info("此时任务个数为：{}", tasks.size());
            log.info("当前任务taskId：{}，taskName：{}，办理人：{}", tasks.get(0).getId(),
                    tasks.get(0).getName(), tasks.get(0).getAssignee());
            //查询执行实例
            List<Execution> executionList3 = executionQuery.list();
            log.info("到达并行多实例节点后，执行实例数为：{}，分别为：{}", executionList3.size(),
                    JSON.toJSONString(executionList3, executionFilter));
            //查询流程变量
            List<Map<String, Object>> varMap3 = getAllVariables(procInst.getId());
            log.info("当前流程变量为：{}", JSON.toJSONString(varMap3));
            taskService.complete(tasks.get(0).getId());
        }

        //查询流程变量
        List<Map<String, Object>> varMap4 = getAllVariables(procInst.getId());
        log.info("当前流程变量为：{}", JSON.toJSONString(varMap4));
        //单实例用户任务
        Task task = taskQuery.singleResult();
        log.info("即将完成最后一个节点的任务，当前任务taskId：{}，taskName：{}",
                task.getId(), task.getName());
        taskService.complete(task.getId());
        //查询执行实例
        List<Execution> executionList4 = executionQuery.list();
        log.info("流程结束后，执行实例数为：{}，分别为：{}", executionList4.size(),
                JSON.toJSONString(executionList4, executionFilter));
    }

    //查询所有流程变量
    private List<Map<String, Object>> getAllVariables(String processInstanceId) {
        //查询所有执行实例
        List<Execution> executionList = runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId).list();
        //获取所有执行实例编号
        Set<String> executionIds = executionList.stream()
                .map(e -> e.getId()).distinct().collect(Collectors.toSet());
        //根据执行实例编号查询流程变量
        List<VariableInstance> variableInstances = runtimeService
                .getVariableInstancesByExecutionIds(executionIds);
        List<Map<String, Object>> variables = new ArrayList<>();
        for (VariableInstance variableInstance : variableInstances) {
            Map<String, Object> varMap = ImmutableMap
                    .of("processInstanceId", variableInstance.getProcessInstanceId(),
                            "name", variableInstance.getName(),
                            "value", variableInstance.getValue(),
                            "executionId",variableInstance.getExecutionId());
            variables.add(varMap);
        }
        return variables;
    }
}