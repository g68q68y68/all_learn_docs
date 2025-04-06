package com.bpm.example.demo3.handler;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.jobexecutor.TimerEventHandler;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.job.service.JobHandler;
import org.flowable.job.service.impl.persistence.entity.JobEntity;
import org.flowable.variable.api.delegate.VariableScope;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Slf4j
public class TimeoutReminderJobHandler extends TimerEventHandler implements JobHandler {

    public static final String TYPE = "timeout-reminder";

    public String getType() {
        return TYPE;
    }

    public void execute(JobEntity job, String configuration, VariableScope variableScope,
                        CommandContext commandContext) {
        //获取传递给作业处理器的参数转换为原始类型
        Map<String, String> userInfo = JSONObject.parseObject(configuration, Map.class);
        //获取流程引擎配置
        ProcessEngineConfiguration processEngineConfiguration
                = CommandContextUtil.getProcessEngineConfiguration(commandContext);
        //获取TaskService
        TaskService taskService = processEngineConfiguration.getTaskService();
        //查询待办任务数
        long taskNum = taskService.createTaskQuery()
                .taskCandidateOrAssigned(userInfo.get("id")).count();
        //获取年月日时分秒毫秒组成的时间戳
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String dataTime = sdf.format(new Date());
        log.info("截至{}，{}存在{}个未处理工作项！", dataTime, userInfo.get("name"), taskNum);
    }
}
