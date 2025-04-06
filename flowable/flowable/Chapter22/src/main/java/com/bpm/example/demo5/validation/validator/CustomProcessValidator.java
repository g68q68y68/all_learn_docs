package com.bpm.example.demo5.validation.validator;

import org.flowable.validation.ProcessValidatorImpl;
import org.flowable.validation.validator.ValidatorSet;
import org.flowable.validation.validator.ValidatorSetFactory;

public class CustomProcessValidator extends ProcessValidatorImpl {
    public CustomProcessValidator() {
        //加入Flowable默认校验规则
        this.addValidatorSet((new ValidatorSetFactory())
                .createFlowableExecutableProcessValidatorSet());
        //加入自定义校验规则
        ValidatorSet customValidatorSet = new ValidatorSet("custom-validtor");
        customValidatorSet.addValidator(new CustomValidator());
        this.addValidatorSet(customValidatorSet);
    }
}