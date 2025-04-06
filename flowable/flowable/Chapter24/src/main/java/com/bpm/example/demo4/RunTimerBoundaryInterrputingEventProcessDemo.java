package com.bpm.example.demo4;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.job.service.impl.persistence.entity.TimerJobEntity;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;
import java.util.List;

@Slf4j
public class RunTimerBoundaryInterrputingEventProcessDemo extends FlowableEngineUtil {
    @Test
    public void runTimerBoundaryInterrputingEventProcessDemo() throws Exception {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.timer.xml");
        //部署流程
        deployResource("processes/TimerBoundaryEventProcess.bpmn20.xml");

        //启动流程实例
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("timerBoundaryEventProcess");
        //查询并办理第一个用户任务
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        Task task1 = taskQuery.singleResult();
        taskService.complete(task1.getId());
        //查询第二个用户任务
        Task task2 = taskQuery.singleResult();
        log.info("任务创建时间：{}，过期时间：{}", task2.getCreateTime(), task2.getDueDate());
        //查询定时任务
        managementService.executeCommand(new Command<Void>() {
            @Override
            public Void execute(CommandContext commandContext) {
                List <TimerJobEntity> list = CommandContextUtil.getTimerJobService()
                        .findTimerJobsByProcessInstanceId(procInst.getId());
                list.stream().forEach(timerJob -> log.info("TimerJob创建时间：{}，触发时间：{}",
                        timerJob.getCreateTime(), timerJob.getDuedate()));
                return null;
            }
        });
    }
}