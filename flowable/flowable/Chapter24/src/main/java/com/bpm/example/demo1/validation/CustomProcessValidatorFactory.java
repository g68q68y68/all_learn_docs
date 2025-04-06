package com.bpm.example.demo1.validation;

import com.bpm.example.demo1.validation.validator.CustomServiceTaskValidator;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ProcessValidatorImpl;
import org.flowable.validation.validator.ValidatorSet;
import org.flowable.validation.validator.ValidatorSetFactory;
import org.flowable.validation.validator.impl.ServiceTaskValidator;

public class CustomProcessValidatorFactory extends ProcessValidatorFactory {

    @Override
    public ProcessValidator createDefaultProcessValidator() {
        //初始化流程校验器
        ProcessValidatorImpl processValidator = new ProcessValidatorImpl();
        //获取Flowable默认流程元素校验器
        ValidatorSet validatorSet = new ValidatorSetFactory()
                .createFlowableExecutableProcessValidatorSet();
        //移除ServiceTask的默认校验器
        validatorSet.removeValidator(ServiceTaskValidator.class);
        //加入自定义的校验器
        validatorSet.addValidator(new CustomServiceTaskValidator());
        processValidator.addValidatorSet(validatorSet);
        return processValidator;
    }
}