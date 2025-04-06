package com.bpm.example.boundaryevent.demo.delegate;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

@Slf4j
public class AutomaticReviewService implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        String healthCodeStatus = (String)execution.getVariable("healthCodeStatus");
        if (!"green".equals(healthCodeStatus)) {
            String errorCode = "healthCodeNotGreen";
            log.error("健康码异常，抛出BPMN错误，errorCode为：{}", errorCode);
            throw new BpmnError(errorCode);
        }
    }
}