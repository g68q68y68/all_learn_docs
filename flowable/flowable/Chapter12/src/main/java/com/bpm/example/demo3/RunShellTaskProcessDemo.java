package com.bpm.example.demo3;

import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RunShellTaskProcessDemo extends FlowableEngineUtil {
    @Test
    public void runShellTaskProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/ShellTaskProcess.bpmn20.xml");
        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("shellTaskProcess");
        //查询用户任务
        Task userTask = taskService.createTaskQuery()
                .processInstanceId(procInst.getId()).singleResult();
        //设置流程变量
        Map<String, Object> varMap = ImmutableMap.of("browserLocation",
                "C:\\Program Files (x86)\\GoogleChrome\\App\\Chrome\\chrome.exe",
                "webUrl",
                "https://www.epubit.com/bookDetails?id=UBd189db7e65bd");
        //完成第一个任务
        taskService.complete(userTask.getId(), varMap);
    }
}