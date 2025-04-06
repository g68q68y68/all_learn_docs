package com.bpm.example.usertask.demo3;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class RunAssigneeTaskByTaskListenerProcessDemo extends FlowableEngineUtil {
    static SimplePropertyPreFilter identityLinkFilter = new SimplePropertyPreFilter(IdentityLink.class,
            "type","userId","groupId");

    @Test
    public void runAssigneeTaskByTaskListenerProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/AssigneeTaskByTaskListenerProcess.bpmn20.xml");

        //设置流程变量
        Map varMap = ImmutableMap.of("designatedUserName", "wangjunlin");
        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("assigneeTaskByTaskListenerProcess", varMap);
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询并完成第一个任务
        Task firstTask = taskQuery.singleResult();
        log.info("当前任务名称为：{}，办理人为：{}", firstTask.getName(), firstTask.getAssignee());
        taskService.complete(firstTask.getId());
        //查询第二个任务
        Task secondTask = taskQuery.singleResult();
        //查询任务的候选人信息
        List<IdentityLink> identityLinkList1 = taskService
                .getIdentityLinksForTask(secondTask.getId());
        log.info("当前任务名称为：{}，候选用户为：{}", secondTask.getName(), JSON.toJSONString(identityLinkList1, identityLinkFilter));
        //候选人liuxiaopeng认领第二个任务
        taskService.claim(secondTask.getId(), "liuxiaopeng");
        //完成第二个任务
        taskService.complete(secondTask.getId());
        //查询第三个任务
        Task thirdTask = taskQuery.singleResult();
        //查询任务的候选组信息
        List<IdentityLink> identityLinkList2 = taskService
                .getIdentityLinksForTask(thirdTask.getId());
        log.info("当前任务名称为：{}，候选用户组为：{}", thirdTask.getName(), JSON.toJSONString(identityLinkList2, identityLinkFilter));
        //候选人xuqiangwei认领第三个任务（用户xuqiangwei是用户组group1的成员）
        taskService.claim(thirdTask.getId(), "xuqiangwei");
        //完成第三个任务
        taskService.complete(thirdTask.getId());
        //查询并完成第四个任务
        Task fourthTask = taskQuery.singleResult();
        log.info("当前任务名称为：{}，办理人为：{}", fourthTask.getName(), fourthTask.getAssignee());
        taskService.complete(fourthTask.getId());
    }
}
