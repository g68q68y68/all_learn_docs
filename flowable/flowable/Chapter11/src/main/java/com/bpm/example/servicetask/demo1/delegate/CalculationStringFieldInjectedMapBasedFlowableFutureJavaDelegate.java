package com.bpm.example.servicetask.demo1.delegate;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.MapBasedFlowableFutureJavaDelegate;
import org.flowable.engine.delegate.ReadOnlyDelegateExecution;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Setter
public class CalculationStringFieldInjectedMapBasedFlowableFutureJavaDelegate implements
        MapBasedFlowableFutureJavaDelegate {
    private Expression unitPrice;
    private Expression quantity;
    private Expression description;

    @Override
    public Map<String, Object> execute(ReadOnlyDelegateExecution execution) {
        //读取注入的unitPrice、quantity和description字段值
        double unitPriceNum = Double.valueOf((String) unitPrice.getValue(execution));
        int quantityNum = Integer.valueOf((String) quantity.getValue(execution));
        String descriptionStr = (String) description.getValue(execution);
        double totalAmount = unitPriceNum * quantityNum;
        log.info("单价：{}，数量：{}，总价：{}", unitPriceNum, quantityNum, totalAmount);
        log.info("描述信息：{}", descriptionStr);
        Map map = new HashMap();
        map.put("totalAmount", totalAmount);
        return map;
    }
}
