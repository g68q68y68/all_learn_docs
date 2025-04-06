package com.bpm.example.demo2.service;

import com.bpm.example.demo2.cmd.TaskCarbonCopyCmd;
import lombok.AllArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.List;

@AllArgsConstructor
public class TaskCarbonCopyService {
    private ManagementService managementService;
    private TaskService taskService;
    private HistoryService historyService;

    public void executeTaskCarbonCopy(String taskId, List<String> userIds) {
        //实例化任务知会Command类
        TaskCarbonCopyCmd taskCarbonCopyCmd = new TaskCarbonCopyCmd(taskId, userIds);
        //通过ManagementService管理服务执行任务知会Command类
        managementService.executeCommand(taskCarbonCopyCmd);
    }

    /**
     * 查询运行时知会任务
     *
     * @param userId
     * @return
     */
    public List<Task> getCarbonCopyTasks(String userId) {
        List<Task> tasks = taskService.createNativeTaskQuery()
                .sql("select t1.* from ACT_RU_TASK t1 join ACT_RU_IDENTITYLINK t2 on "
                        + "t2.TASK_ID_=t1.ID_ and t2.TYPE_='carbonCopy' and "
                        + "t2.USER_ID_=#{userId}")
                .parameter("userId", userId).list();
        return tasks;
    }

    /**
     * 查询历史知会任务
     *
     * @param userId
     * @return
     */
    public List<HistoricTaskInstance> getHistoricCarbonCopyTasks(String userId) {
        List<HistoricTaskInstance> tasks = historyService
                .createNativeHistoricTaskInstanceQuery()
                .sql("select * from ACT_HI_TASKINST t1 join ACT_HI_IDENTITYLINK t2 on "
                        + "t2.TASK_ID_=t1.ID_ and t2.TYPE_='carbonCopy' and t2.USER_ID_= "
                        + "#{userId}")
                .parameter("userId", userId).list();
        return tasks;
    }
}
