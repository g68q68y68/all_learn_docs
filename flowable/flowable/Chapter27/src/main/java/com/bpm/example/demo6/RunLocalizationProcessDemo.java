package com.bpm.example.demo6;

import com.bpm.common.util.FlowableEngineUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.DynamicBpmnConstants;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.context.BpmnOverrideContext;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Test;

@Slf4j
public class RunLocalizationProcessDemo extends FlowableEngineUtil {
    @Test
    public void runLocalizationProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition procDef = deployResource("processes/LocalizationProcess.bpmn20.xml");
        log.info("默认流程名称：{}，描述：{}", procDef.getName(), procDef.getDescription());
        //查询各语言版本流程名称和描述
        procDef = queryProcessDefinition(procDef.getId(), "en");
        log.info("en版流程名称：{}，描述：{}", procDef.getName(), procDef.getDescription());
        procDef = queryProcessDefinition(procDef.getId(), "fr");
        log.info("fr版流程名称：{}，描述：{}", procDef.getName(), procDef.getDescription());

        //启动流程
        ProcessInstance procInst = runtimeService.startProcessInstanceById(procDef.getId());
        //查询各语言版本任务名称和描述
        Task task = queryTask(procInst.getId(), null);
        log.info("默认任务名称:{}, 描述:{}", task.getName(), task.getDescription());
        task = queryTask(procInst.getId(), "en");
        log.info("en版任务名称:{}, 描述:{}", task.getName(), task.getDescription());
        task = queryTask(procInst.getId(), "fr");
        log.info("fr版任务名称:{}, 描述:{}", task.getName(), task.getDescription());

        ObjectNode infoNode = dynamicBpmnService
                .getProcessDefinitionInfo(procInst.getProcessDefinitionId());
        //修改法语版流程名称和描述
        dynamicBpmnService.changeLocalizationName("fr", procDef.getKey(),
                "Mise en ligne du système", infoNode);
        dynamicBpmnService.changeLocalizationDescription("fr", procDef.getKey(),
                "Ce lien est utilisé pour la mise en ligne du système d'exploitation humaine",
                infoNode);
        //添加韩文版流程名称和描述
        dynamicBpmnService.changeLocalizationName("ko", procDef.getKey(),
                "시스템 온라인 프로세스", infoNode);
        dynamicBpmnService.changeLocalizationDescription("ko", procDef.getKey(),
                "이것은 시스템 온라인에서의 수동 처리 과정입니다", infoNode);
        //添加韩文版任务名称和描述
        dynamicBpmnService.changeLocalizationName("ko", task.getTaskDefinitionKey(),
                "시스템 온라인", infoNode);
        dynamicBpmnService.changeLocalizationDescription("ko",
                task.getTaskDefinitionKey(),
                "이 단계는 인공 운영 시스템의 온라인 상태를 수동으로 조작하는 데 사용됩니다",
                infoNode);
        //保存
        dynamicBpmnService.saveProcessDefinitionInfo(procInst.getProcessDefinitionId(),
                infoNode);

        //查询各语言版本任务名称和描述
        procDef = queryProcessDefinition(procDef.getId(), "fr");
        log.info("修改后，fr版流程名称：{}，描述：{}", procDef.getName(), procDef.getDescription());
        procDef = queryProcessDefinition(procDef.getId(), "ko");
        log.info("ko版流程名称：{}，描述：{}", procDef.getName(), procDef.getDescription());
        //查询各语言版本任务名称和描述
        task = queryTask(procInst.getId(), "ko");
        log.info("ko版任务名称:{}, 描述:{}", task.getName(), task.getDescription());
    }

    /**
     * 查询流程定义信息
     *
     * @param processDefinitionId 流程定义编号
     * @param locale              语种标识
     * @return
     */
    private ProcessDefinition queryProcessDefinition(String processDefinitionId,
                                                     String locale) {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService
                .createProcessDefinitionQuery();
        if (StringUtils.isNotBlank(processDefinitionId)) {
            processDefinitionQuery.processDefinitionId(processDefinitionId);
        }
        if (StringUtils.isNotBlank(locale)) {
            processDefinitionQuery.locale(locale);
        }
        return processDefinitionQuery.singleResult();
    }

    /**
     * 查询任务信息
     *
     * @param processInstanceId 流程定义编号
     * @param locale            语种标识
     * @return
     */
    private Task queryTask(String processInstanceId, String locale) {
        TaskQuery taskQuery = taskService.createTaskQuery();
        if (StringUtils.isNotBlank(processInstanceId)) {
            taskQuery.processInstanceId(processInstanceId);
        }
        if (StringUtils.isNotBlank(locale)) {
            taskQuery.locale(locale);
        }
        return taskQuery.singleResult();
    }
}
