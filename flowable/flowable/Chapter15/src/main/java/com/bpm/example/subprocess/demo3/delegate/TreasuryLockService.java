package com.bpm.example.subprocess.demo3.delegate;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

@Slf4j
public class TreasuryLockService implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        log.info("锁定库存完成！");
    }
}
