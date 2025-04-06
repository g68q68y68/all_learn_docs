package com.bpm.example.demo3;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.List;

@Slf4j
public class RunCustomActivityBehaviorProcessDemo extends FlowableEngineUtil {
    SimplePropertyPreFilter taskFilter = new SimplePropertyPreFilter(Task.class,
            "taskId", "name", "assignee");
    SimplePropertyPreFilter identityLinkFilter
            = new SimplePropertyPreFilter(IdentityLink.class, "taskId", "type", "userId");

    @Test
    public void runCustomActivityBehaviorDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.custom-activitybehavior.xml");
        //部署流程
        deployResource("processes/CustomActivityBehaviorProcess.bpmn20.xml");

        //启动流程实例
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("customActivityBehaviorProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询第一个用户任务
        Task firstTask = taskQuery.singleResult();
        log.info("第一个用户任务：{}", JSON.toJSONString(firstTask, taskFilter));
        //办理完成第一个用户任务
        taskService.complete(firstTask.getId());
        //查询第二个用户任务
        Task secondTask = taskQuery.singleResult();
        log.info("第二个用户任务：{}", JSON.toJSONString(secondTask, taskFilter));
        List<IdentityLink> identityLinks = taskService
                .getIdentityLinksForTask(secondTask.getId());
        log.info("办理/候选人：{}", JSON.toJSONString(identityLinks, identityLinkFilter));
        //办理完成第二个用户任务
        taskService.complete(secondTask.getId());
    }
}
