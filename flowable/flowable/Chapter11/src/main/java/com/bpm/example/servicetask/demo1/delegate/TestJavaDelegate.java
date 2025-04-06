package com.bpm.example.servicetask.demo1.delegate;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

@Slf4j
@Setter
public class TestJavaDelegate implements JavaDelegate {

    public void execute(DelegateExecution execution) {
        for (int i = 0;i <= 50; i++) {
            log.info(execution.getCurrentActivityId()+ "：" + i);
        }
    }
}