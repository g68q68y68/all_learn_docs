package com.bpm.example.demo5;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ValidationError;
import org.junit.Test;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.List;

@Slf4j
public class RunCustomValidatorProcessDemo extends FlowableEngineUtil {
    @Test
    public void runCustomValidatorProcessDemo() throws Exception {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.custom-validation.xml");
        //解析流程xml
        XMLInputFactory factory = XMLInputFactory.newFactory();
        InputStream stream = getClass().getClassLoader()
                .getResourceAsStream("processes/CustomValidatorProcess.bpmn20.xml");
        XMLStreamReader reader = factory.createXMLStreamReader(stream);
        //将流程xml转换为BpmnModel
        BpmnXMLConverter converter = new BpmnXMLConverter();
        BpmnModel bpmnModel = converter.convertToBpmnModel(reader);
        //获取自定义流程校验器
        ProcessValidator processValidator = processEngineConfiguration
                .getProcessValidator();
        //进行模型校验
        List<ValidationError> validate = processValidator.validate(bpmnModel);
        //如果校验错误集合长度大于1，则说明校验出错，遍历打印出错信息
        if (validate.size() >= 1) {
            for (ValidationError validationError : validate) {
                log.info("{}校验异常：{}", validationError.getValidatorSetName(),
                        validationError.getProblem());
            }
        }
    }
}
