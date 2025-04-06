package com.bpm.example.demo4;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

@Slf4j
public class RunMigrateProcessInstanceFromOldToNewDemo extends FlowableEngineUtil {
    @Test
    public void runMigrateProcessInstanceFromOldToNewDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition oldProDef = deployResource("processes"
                + "/MigrateProcessInstanceOldProcess.bpmn20.xml");
        ProcessDefinition newProDef = deployResource("processes"
                + "/MigrateProcessInstanceNewProcess.bpmn20.xml");

        //启动旧版下的流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceById(oldProDef.getId());
        log.info("迁移前流程实例所在的流程定义为{}", procInst.getProcessDefinitionId());
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processInstanceId(procInst.getId());
        //查询并完成第一个任务
        Task firstTask = taskQuery.singleResult();
        taskService.complete(firstTask.getId());

        //流程迁移校验
        boolean migrationValid = processMigrationService
                .createProcessInstanceMigrationBuilder()
                .migrateToProcessDefinition(newProDef.getId())
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
                .migrateToProcessDefinition(newProDef.getId())
                .migrate(procInst.getId());
        procInst = runtimeService.createProcessInstanceQuery()
                .processInstanceId(procInst.getId()).singleResult();
        log.info("迁移后流程实例所在的流程定义为{}", procInst.getProcessDefinitionId());
    }
}
