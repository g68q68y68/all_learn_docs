package com.bpm.example.startevent.demo.delegate;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

//财务审计服务
@Slf4j
public class ReportFinanceOfficerDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("发现财务审计异常，上报财务主管！");
    }
}