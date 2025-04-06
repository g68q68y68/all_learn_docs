package com.bpm.example.servicetask.demo1.delegate;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

@Slf4j
@Setter
public class CalculationStringFieldInjectedJavaDelegate implements JavaDelegate {
    private Expression unitPrice;
    private Expression quantity;
    private Expression description;

    public void execute(DelegateExecution execution) {
        //读取注入的unitPrice、quantity和description字段值
        double unitPriceNum = Double.valueOf((String) unitPrice.getValue(execution));
        int quantityNum = Integer.valueOf((String) quantity.getValue(execution));
        String descriptionStr = (String) description.getValue(execution);
        double totalAmount = unitPriceNum * quantityNum;
        log.info("单价：{}，数量：{}，总价：{}", unitPriceNum, quantityNum, totalAmount);
        log.info("描述信息：{}", descriptionStr);
        //将totalAmount放入流程变量中
        execution.setVariable("totalAmount", totalAmount);
    }
}