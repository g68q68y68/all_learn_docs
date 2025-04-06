package com.bpm.example.servicetask.demo2.delegate;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

@Slf4j
@Setter
public class CalculationUelFieldInjectedJavaDelegate implements JavaDelegate {
    private Expression inventoryCheckResult;
    private Expression totalAmount;
    private Expression description;

    @Override
    public void execute(DelegateExecution execution) {
        //读取注入的inventoryCheckResult字段值
        String inventoryCheckResultStr = (String) inventoryCheckResult.getValue(execution);
        if ("库存不足".equals(inventoryCheckResultStr)) {
            log.error("库存不足！");
            return;
        }
        //读取注入的totalAmount字段值
        double totalAmountNum = (double) totalAmount.getValue(execution);
        //读取注入的description字段值
        String descriptionStr = (String) description.getValue(execution);
        //从流程变量中获取unitPrice、quantity的值
        double unitPrice = (double)execution.getVariable("unitPrice");
        int quantity = (int)execution.getVariable("quantity");
        log.info("单价：{}，数量：{}，总价：{}", unitPrice, quantity, totalAmountNum);
        log.info("描述信息：{}", descriptionStr);
        //将totalAmount放入流程变量中
        execution.setVariable("totalAmount", totalAmountNum);
    }
}