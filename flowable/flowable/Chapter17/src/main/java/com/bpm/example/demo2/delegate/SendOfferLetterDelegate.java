package com.bpm.example.demo2.delegate;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

@Slf4j
public class SendOfferLetterDelegate implements JavaDelegate {
    @Setter
    Expression userIdField;
    @Override
    public void execute(DelegateExecution execution) {
        //获取loopCounter、userId变量的值
        int loopCounter = (Integer) execution.getVariable("loopCounter");
        String userId = (String)execution.getVariable("userId");
        //获取属性注入的userIdField属性的值
        Object userIdFieldValue = userIdField.getValue(execution);
        log.info("第{}位录取人员{}的录用通知发送成功！", (loopCounter + 1), userId);
        log.info("通过属性注入的userIdField属性值为：{}", userIdFieldValue);
    }
}