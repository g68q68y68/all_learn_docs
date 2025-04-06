package com.bpm.example.endevent.demo.delegate;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

@Slf4j
public class CompensationForCancelEndEventDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        log.info("执行补偿操作！");
    }
}
