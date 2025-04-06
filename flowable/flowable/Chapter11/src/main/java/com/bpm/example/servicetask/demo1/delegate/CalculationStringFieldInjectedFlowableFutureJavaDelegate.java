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
public class CalculationStringFieldInjectedFlowableFutureJavaDelegate implements
        FlowableFutureJavaDelegate {
    private Expression unitPrice;
    private Expression quantity;
    private Expression description;

    @Override
    public Object prepareExecutionData(DelegateExecution execution) {
        //读取注入的unitPrice、quantity和description字段值
        double unitPriceNum = Double.valueOf((String) unitPrice.getValue(execution));
        int quantityNum = Integer.valueOf((String) quantity.getValue(execution));
        String descriptionStr = (String) description.getValue(execution);
        Map map = new HashMap();
        map.put("unitPriceNum", unitPriceNum);
        map.put("quantityNum", quantityNum);
        map.put("descriptionStr", descriptionStr);
        return map;
    }

    @Override
    public Object execute(Object inputData) {
        Map map = (Map) inputData;
        double unitPriceNum = (double) map.get("unitPriceNum");
        int quantityNum = (int) map.get("quantityNum");
        String descriptionStr = (String) map.get("descriptionStr");
        double totalAmount = unitPriceNum * quantityNum;
        log.info("单价：{}，数量：{}，总价：{}", unitPriceNum, quantityNum, totalAmount);
        log.info("描述信息：{}", descriptionStr);
        return totalAmount;
    }

    @Override
    public void afterExecution(DelegateExecution execution, Object executionData) {
        execution.setVariable("totalAmount", executionData);
    }
}
