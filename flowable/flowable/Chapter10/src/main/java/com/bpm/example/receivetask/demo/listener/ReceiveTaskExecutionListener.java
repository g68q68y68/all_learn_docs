package com.bpm.example.receivetask.demo.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

@Slf4j
public class ReceiveTaskExecutionListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {
        FlowElement currentFlowElement = execution.getCurrentFlowElement();
        log.info("当前为接收任务，节点名称：{}，备注：{}", currentFlowElement.getName(),
                currentFlowElement.getDocumentation());
        String result = (String) execution.getVariable("result");
        log.info("接收任务已被触发，处理结果为：{}", result);
    }
}
