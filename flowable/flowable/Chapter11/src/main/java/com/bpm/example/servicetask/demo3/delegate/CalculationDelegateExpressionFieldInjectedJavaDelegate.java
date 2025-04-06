package com.bpm.example.servicetask.demo3.delegate;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.DelegateHelper;
import org.flowable.engine.delegate.JavaDelegate;

@Slf4j
public class CalculationDelegateExpressionFieldInjectedJavaDelegate implements JavaDelegate {
    @Setter
    private Expression inventoryCheckResult;

    @Override
    public void execute(DelegateExecution execution) {
        //读取注入的inventoryCheckResult字段值
        String inventoryCheckResultStr = (String) inventoryCheckResult.getValue(execution);
        if ("库存不足".equals(inventoryCheckResultStr)) {
            log.error("库存不足！");
            return;
        }
        //读取注入的totalAmount字段值
        Expression totalAmountNumExpression = DelegateHelper
                .getFieldExpression(execution, "totalAmount");
        double totalAmountNum = (double) totalAmountNumExpression.getValue(execution);
        //读取注入的description字段值
        Expression descriptionExpression = DelegateHelper
                .getFieldExpression(execution, "description");
        String descriptionStr = (String) descriptionExpression.getValue(execution);
        //从流程变量中获取unitPrice、quantity的值
        double unitPrice = (double) execution.getVariable("unitPrice");
        int quantity = (int) execution.getVariable("quantity");
        log.info("单价：{}，数量：{}，总价：{}", unitPrice, quantity, totalAmountNum);
        log.info("描述信息：{}", descriptionStr);
        //将totalAmount放入流程变量中
        execution.setVariable("totalAmount", totalAmountNum);
    }
}
