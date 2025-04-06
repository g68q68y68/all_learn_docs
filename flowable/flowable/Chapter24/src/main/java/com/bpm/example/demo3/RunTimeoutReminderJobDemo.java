package com.bpm.example.demo3;

import com.alibaba.fastjson.JSONObject;
import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo3.handler.TimeoutReminderJobHandler;
import com.google.common.collect.ImmutableMap;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.job.service.JobServiceConfiguration;
import org.flowable.job.service.impl.asyncexecutor.JobManager;
import org.flowable.job.service.impl.persistence.entity.JobEntity;
import org.flowable.job.service.impl.persistence.entity.TimerJobEntity;
import org.flowable.job.service.impl.persistence.entity.TimerJobEntityManager;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

public class RunTimeoutReminderJobDemo extends FlowableEngineUtil {

    @Test
    public void runTimeoutReminderJobDemo() throws Exception {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.job.xml");
        //部署流程
        ProcessDefinition processDefinition
                = deployResource("processes/UserTaskProcess.bpmn20.xml");

        //创建10个流程实例
        for (int i = 0; i < 10; i++) {
            runtimeService.startProcessInstanceById(processDefinition.getId());
        }

        //自定义命令
        Command customTimerJobCommand = new Command<Void>() {
            @Override
            public Void execute(CommandContext commandContext) {
                //获取JobServiceConfiguration
                JobServiceConfiguration jobServiceConfiguration =
                        processEngineConfiguration.getJobServiceConfiguration();
                //获取TimerJob实体管理器
                TimerJobEntityManager timerJobEntityManager = jobServiceConfiguration
                        .getTimerJobEntityManager();
                //创建TimerJob对象
                TimerJobEntity timer = timerJobEntityManager.create();
                //设置TimerJob类型
                timer.setJobType(JobEntity.JOB_TYPE_TIMER);
                //设置作业处理器
                timer.setJobHandlerType(TimeoutReminderJobHandler.TYPE);
                //设置传递给作业处理器的参数
                Map<String, Object> userInfo = ImmutableMap.of("id", "liuxiaopeng",
                        "name", "刘晓鹏");
                timer.setJobHandlerConfiguration(JSONObject.toJSONString(userInfo));
                //设置定时任务执行周期
                timer.setRepeat("R/PT2M");
                timer.setRetries(processEngineConfiguration.getAsyncExecutorNumberOfRetries());
                timer.setExclusive(true);

                //时间计算
                Date now = new Date();
                //delay为相较当前时间，延时的时间变量
                Date target = new Date(now.getTime() + 10 * 1000);
                //设置当前定时任务的触发时间
                timer.setDuedate(target);

                //保存并触发定时任务
                JobManager jobManager = jobServiceConfiguration.getJobManager();
                jobManager.scheduleTimerJob(timer);
                return null;
            }
        };
        //执行自定义命令
        managementService.executeCommand(customTimerJobCommand);
        //主线程暂停
        Thread.sleep(1000 * 60 * 10);
    }
}