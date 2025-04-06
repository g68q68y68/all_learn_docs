package com.bpm.example.manualtask.demo.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

@Slf4j
public class ManualTaskExecutionListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {
        //获取当前节点信息
        FlowElement currentFlowElement = execution.getCurrentFlowElement();
        log.info("到达手动任务，当前节点名称：{}，备注：{}", currentFlowElement.getName(),
                currentFlowElement.getDocumentation());
        log.info("处理结果：奖品线下发放完成！");
    }
}
