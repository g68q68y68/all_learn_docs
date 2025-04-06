package com.bpm.example.demo5;

import com.alibaba.fastjson.JSON;
import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo5.mapper.CustomSqlMapper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.cmd.CustomSqlExecution;
import org.flowable.engine.impl.cmd.AbstractCustomSqlExecution;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class RunCustomSqlProcessDemo extends FlowableEngineUtil {
    @Test
    public void runCustomSqlProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.custom-mybatis-mapper.xml");
        //部署流程
        deployResource("processes/CustomSqlProcess.bpmn20.xml");

        //启动流程
        String businessKey = UUID.randomUUID().toString();
        runtimeService.startProcessInstanceByKey("customSqlProcess", businessKey);

        //配置CustomSqlExecution调用自定义MyBatis
        // Mapper类中的customSelectProcessInstanceByBusinessKey接口进行查询
        CustomSqlExecution<CustomSqlMapper, Map<String, String>> customSqlExecution1
                = new AbstractCustomSqlExecution<CustomSqlMapper, Map<String, String>>
                (CustomSqlMapper.class) {
            @Override
            public Map<String, String> execute(CustomSqlMapper customSqlMapper) {
                return customSqlMapper.customSelectProcessInstanceByBusinessKey(businessKey);
            }
        };
        Map<String, String> processMap = managementService
                .executeCustomSql(customSqlExecution1);
        log.info("流程实例信息为：{}", JSON.toJSONString(processMap));

        //配置CustomSqlExecution调用自定义MyBatis Mapper类中的customSelectTasksByProcessId接口进行查询
        CustomSqlExecution<CustomSqlMapper, List<TaskEntityImpl>> customSqlExecution2
                = new AbstractCustomSqlExecution<CustomSqlMapper, List<TaskEntityImpl>>(CustomSqlMapper.class) {
            @Override
            public List<TaskEntityImpl> execute(CustomSqlMapper customSqlMapper) {
                return customSqlMapper.customSelectTasksByProcessId(processMap.get("ID_"));
            }
        };
        List<TaskEntityImpl> tasks = managementService.executeCustomSql(customSqlExecution2);
        log.info("流程中的待办任务个数有：{}", tasks.size());
        TaskEntity task = tasks.get(0);

        //配置CustomSqlExecution调用自定义MyBatis Mapper类中的customSelectIdentityLinkByTaskId接口进行查询
        CustomSqlExecution<CustomSqlMapper, List<Map<String, String>>> customSqlExecution3
                = new AbstractCustomSqlExecution<CustomSqlMapper, List<Map<String, String>>>(CustomSqlMapper.class) {
            @Override
            public List<Map<String, String>> execute(CustomSqlMapper customSqlMapper) {
                return customSqlMapper.customSelectIdentityLinkByTaskId(task.getId());
            }
        };
        List<Map<String, String>> identityLinks = managementService.executeCustomSql
                (customSqlExecution3);
        log.info("流程任务及候选人信息为：{}", JSON.toJSONString(identityLinks));
    }
}