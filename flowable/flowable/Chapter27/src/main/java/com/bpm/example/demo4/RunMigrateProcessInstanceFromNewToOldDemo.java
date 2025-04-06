package com.bpm.example.demo4;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

import java.util.List;

@Slf4j
public class RunMigrateProcessInstanceFromNewToOldDemo extends FlowableEngineUtil {
    @Test
    public void runMigrateProcessInstanceFromNewToOldDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition oldProcDef = deployResource("processes"
                + "/MigrateProcessInstanceOldProcess.bpmn20.xml");
        ProcessDefinition newProcDef = deployResource("processes"
                + "/MigrateProcessInstanceNewProcess.bpmn20.xml");

        //启动新版下的流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceById(newProcDef.getId());
        log.info("迁移前流程实例所在的流程定义为{}", procInst.getProcessDefinitionId());
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询并完成第一个任务
        Task firstTask = taskQuery.singleResult();
        taskService.complete(firstTask.getId());
        //办理完成直属上级审批、物料管理员审批的任务
        List<Task> taskList = taskQuery.list();
        taskList.stream().forEach(task -> {
            log.info("办理完成任务：{}", task.getName());
            taskService.complete(task.getId());
        });

        //流程迁移校验
        boolean migrationValid = processMigrationService
                .createProcessInstanceMigrationBuilder()
                .migrateToProcessDefinition(oldProcDef.getId())
                .validateMigration(procInst.getId())
                .isMigrationValid();
        log.info("流程迁移校验结果为：{}", migrationValid);
        if (!migrationValid) {
            //校验不通过
            log.info("迁移校验不通过，无法迁移！");
            return;
        }

        //校验通过后执行流程迁移
        processMigrationService.createProcessInstanceMigrationBuilder()
                .migrateToProcessDefinition(oldProcDef.getId())
                .migrate(procInst.getId());
        procInst = runtimeService.createProcessInstanceQuery()
                .processInstanceId(procInst.getId()).singleResult();
        log.info("迁移后流程实例所在的流程定义为{}", procInst.getProcessDefinitionId());
    }
}
