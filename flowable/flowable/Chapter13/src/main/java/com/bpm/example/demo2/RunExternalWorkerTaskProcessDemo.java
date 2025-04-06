package com.bpm.example.demo2;

import com.alibaba.fastjson.JSON;
import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.job.api.AcquiredExternalWorkerJob;
import org.flowable.job.api.ExternalWorkerJob;
import org.flowable.task.api.Task;
import org.junit.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class RunExternalWorkerTaskProcessDemo extends FlowableEngineUtil {
    @Test
    public void runExternalWorkerTaskProcessDemo() throws Exception {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition processDefinition
                = deployResource("processes/ExternalWorkerTaskProcess.bpmn20.xml");

        //启动流程
        ProcessInstance procInst1 = runtimeService
                .startProcessInstanceById(processDefinition.getId());
        ProcessInstance procInst2 = runtimeService
                .startProcessInstanceById(processDefinition.getId());
        //初始化变量
        Map<String, Object> varMap1 = ImmutableMap.of("batchNo", "batch001");
        Map<String, Object> varMap2 = ImmutableMap.of("batchNo", "batch002");
        //查询并办理第一个任务
        Task task1_1 = taskService.createTaskQuery()
                .processInstanceId(procInst1.getId()).singleResult();
        Task task2_1 = taskService.createTaskQuery()
                .processInstanceId(procInst2.getId()).singleResult();
        taskService.complete(task1_1.getId(), varMap1);
        taskService.complete(task2_1.getId(), varMap2);
        //查询任务
        List<ExternalWorkerJob> externalWorkerJobs = managementService
                .createExternalWorkerJobQuery().list();
        log.info("查询到的外部工作者作业数：{}", externalWorkerJobs.size());
        //获取并锁定外部工作者作业
        List<AcquiredExternalWorkerJob> acquiredJobs = managementService
                .createExternalWorkerJobAcquireBuilder()
                .topic("batchTopic", Duration.ofMinutes(30))
                .acquireAndLock(5, "worker-1");
        log.info("获取并锁定的外部工作者作业数：{}", acquiredJobs.size());
        //模拟外部工作者处理外部任务，暂停30秒
        Thread.sleep(30 * 1000);
        //第一个外部工作作业正常完成
        managementService
                .createExternalWorkerCompletionBuilder(acquiredJobs.get(0).getId(), "worker-1")
                .variable("result", "COMPLETED")
                .complete();
        //第二个外部工作作业抛出错误
        managementService
                .createExternalWorkerCompletionBuilder(acquiredJobs.get(1).getId(), "worker-1")
                .variable("result", "FAILED")
                .bpmnError("externalWorkerTaskError");
        //等待异步执行，暂停10秒
        Thread.sleep(10 * 1000);
        //查询流程当前任务
        Task task1_2 = taskService.createTaskQuery()
                .processInstanceId(procInst1.getId()).singleResult();
        Task task2_2 = taskService.createTaskQuery()
                .processInstanceId(procInst2.getId()).singleResult();
        log.info("第一个流程当前节点：{}", task1_2.getName());
        log.info("第二个流程当前节点：{}", task2_2.getName());
        //查看变量
        Map<String, Object> varMap3 = runtimeService.getVariables(procInst1.getId());
        Map<String, Object> varMap4 = runtimeService.getVariables(procInst2.getId());
        log.info("第一个流程的变量为：{}", JSON.toJSONString(varMap3));
        log.info("第二个流程的变量为：{}", JSON.toJSONString(varMap4));
    }
}