package com.bpm.example.startevent.demo.delegate;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

//财务审计服务
@Slf4j
public class FinancialAuditDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        log.error("财务审计异常，抛出错误！");
        //抛出错误，子流程的错误开始事件会捕获
        throw new BpmnError("financialErrorCode");
    }
}
