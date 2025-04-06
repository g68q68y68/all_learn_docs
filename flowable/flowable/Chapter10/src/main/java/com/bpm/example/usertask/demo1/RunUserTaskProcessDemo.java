package com.bpm.example.usertask.demo1;
import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Slf4j
public class RunUserTaskProcessDemo extends FlowableEngineUtil {
    @Test
    public void runUserTaskProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        deployResource("processes/UserTaskProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("userTaskProcess");
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询第一个任务
        Task task1 = taskQuery.singleResult();
        log.info("第一个任务taskId：{}，taskName为：{}", task1.getId(), task1.getName());
        log.info("用户任务描述信息为：{}", task1.getDescription());
        log.info("用户任务创建时间为：{}", getStringDate(task1.getCreateTime()));
        //设置流程变量
        Map<String, Object> varMap = ImmutableMap.of("dayNum", 3,
                "applyReason", "休探亲假。");
        //办理第一个任务
        taskService.complete(task1.getId(), varMap);
        //查询第二个任务
        Task task2 = taskQuery.singleResult();
        log.info("第二个任务taskId：{}，taskName为：{}", task2.getId(), task2.getName());
        log.info("用户任务描述信息为：{}", task2.getDescription());
        log.info("用户任务创建时间为：{}，过期时间为：{}", getStringDate(task2.getCreateTime()), getStringDate(task2.getDueDate()));
    }

    /**
     * 转换时间为字符串
     * @param time
     * @return
     */
    private String getStringDate(Date time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(time);
        return dateString;
    }
}