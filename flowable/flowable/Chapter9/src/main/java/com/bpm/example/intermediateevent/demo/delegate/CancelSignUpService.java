package com.bpm.example.intermediateevent.demo.delegate;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

@Slf4j
public class CancelSignUpService implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        log.info("执行补偿，取消正式报名完成！");
    }
}
