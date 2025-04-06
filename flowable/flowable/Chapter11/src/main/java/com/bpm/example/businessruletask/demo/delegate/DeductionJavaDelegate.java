package com.bpm.example.businessruletask.demo.delegate;

import com.bpm.example.businessruletask.demo.model.CostCalculation;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.List;

@Slf4j
public class DeductionJavaDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        List<CostCalculation> list = (List) execution.getVariable("costCalculationResults");
        log.info("折扣前消费金额：{}，折扣率：{}，折扣后实际消费金额：{}",
                list.get(0).getOriginalTotalPrice(),
                list.get(0).getDiscountRatio(),
                list.get(0).getActualTotalPrice());
    }
}
