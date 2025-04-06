package com.bpm.example.demo4;

import com.alibaba.fastjson.JSON;
import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.db.DbSqlSession;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class RunCustomSqlProcessDemo extends FlowableEngineUtil {
    @Test
    public void runCustomSqlProcessDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.custom-mybatis-xml-mapper.xml");
        //部署流程
        deployResource("processes/CustomSqlProcess.bpmn20.xml");

        //启动流程
        String businessKey = UUID.randomUUID().toString();
        runtimeService.startProcessInstanceByKey("customSqlProcess", businessKey);

        //自定义命令
        Command customSqlCommand = new Command<Void>() {
            @Override
            public Void execute(CommandContext commandContext) {
                //从上下文commandContext中获取DbSqlSession对象
                DbSqlSession dbSqlSession = CommandContextUtil.getDbSqlSession(commandContext);
                //通过dbSqlSession的selectOne方法调用自定义MyBatis
                // XML中定义的customSelectProcessInstanceByBusinessKey查询单个结果
                Map<String, String> processMap = (Map) dbSqlSession
                        .selectOne("customSelectProcessInstanceByBusinessKey", businessKey);
                log.info("流程实例信息为：{}", JSON.toJSONString(processMap));
                //通过dbSqlSession的selectList方法调用自定义MyBatis
                // XML中定义的customSelectTasksByProcessId查询结果集
                List<TaskEntity> tasks = dbSqlSession
                        .selectList("customSelectTasksByProcessInstanceId",
                                processMap.get("id"));
                log.info("流程中的待办任务个数有：{}", tasks.size());
                TaskEntity taskEntity = tasks.get(0);
                //通过dbSqlSession的selectList方法调用自定义MyBatis
                // XML中定义的customSelectIdentityLinkByTaskId查询结果集
                List<Map> identityLinks = dbSqlSession
                        .selectList("customSelectIdentityLinkByTaskId", taskEntity.getId());
                log.info("流程任务及候选人信息为：{}", JSON.toJSONString(identityLinks));
                return null;
            }
        };
        //执行自定义命令
        managementService.executeCommand(customSqlCommand);
    }
}