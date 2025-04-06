package com.bpm.example.servicetask.demo1.delegate;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.FlowableFutureJavaDelegate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Setter
public class TestFutureJavaDelegate implements FlowableFutureJavaDelegate {

    @Override
    public Object prepareExecutionData(DelegateExecution execution) {
        String currentActivityId = execution.getCurrentActivityId();
        return currentActivityId;
    }

    @Override
    public Object execute(Object inputData) {
        for (int i = 0;i <= 50; i++) {
            log.info((String)inputData + "：" + i);
        }
        return null;
    }

    @Override
    public void afterExecution(DelegateExecution execution, Object executionData) {

    }
}
