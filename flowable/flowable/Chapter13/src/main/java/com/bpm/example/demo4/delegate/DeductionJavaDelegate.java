package com.bpm.example.demo4.delegate;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

@Slf4j
public class DeductionJavaDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        Number originalTotalPrice = (Number) execution.getVariable("originalTotalPrice");
        Number actualTotalPrice = (Number) execution.getVariable("actualTotalPrice");
        Number discountRatio = (Number) execution.getVariable("discountRatio");
        log.info("折扣前消费金额：{}，折扣率：{}，折扣后实际消费金额：{}", originalTotalPrice,
                discountRatio, actualTotalPrice);
    }
}
