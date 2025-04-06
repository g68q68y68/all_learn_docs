package com.bpm.example;

import com.bpm.example.common.util.FlowableEngineUtil;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;

import java.text.SimpleDateFormat;
import java.util.List;

public class RunDemo9 extends FlowableEngineUtil {
    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void main(String[] args) {
        RunDemo9 demo = new RunDemo9();
        demo.runDemo();
    }

    private void runDemo() {
        //初始化工作流引擎
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition processDefinition =
                deployResource("processes/SimpleProcess.bpmn20.xml");
        //启动3个流程实例
        String businessKeyPrefix = "code_";
        ProcessInstance processInstance1 =
                runtimeService.startProcessInstanceById(processDefinition.getId(),
                        businessKeyPrefix + 1);
        ProcessInstance processInstance2 =
                runtimeService.startProcessInstanceById(processDefinition.getId(),
                        businessKeyPrefix + 2);
        ProcessInstance processInstance3 =
                runtimeService.startProcessInstanceById(processDefinition.getId(),
                        businessKeyPrefix + 3);

        //查询历史流程实例
        List<HistoricProcessInstance> historicProcessInstances =
                historyService.createNativeHistoricProcessInstanceQuery()
                        .sql("select * from ACT_HI_PROCINST where BUSINESS_KEY_ like concat(#{prefix}, '%')")
                        .parameter("prefix", businessKeyPrefix)
                        .list();
        for (HistoricProcessInstance processInstance : historicProcessInstances) {
            System.out.println("流程实例ID：" + processInstance.getId()
                    + "，业务主键：" + processInstance.getBusinessKey()
                    + "，创建时间：" + dateFormat.format(processInstance.getStartTime())
            );
        }
    }
}

