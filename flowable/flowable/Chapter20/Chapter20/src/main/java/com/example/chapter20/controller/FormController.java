package com.example.chapter20.controller;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.FormService;
import org.flowable.engine.TaskService;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.TaskFormData;
import org.flowable.form.api.FormInfo;
import org.flowable.form.model.FormField;
import org.flowable.form.model.SimpleFormModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class FormController {
    @Autowired
    private FormService formService;
    @Autowired
    private TaskService taskService;

    @GetMapping("/getFormData/{taskId}")
    public String getVariables(@PathVariable String taskId) {
        //查找流程变量
//        formService.getRenderedTaskForm()
        TaskFormData taskFormData = formService.getTaskFormData(taskId);
        List<FormProperty> formProperties = taskFormData.getFormProperties();
        //只适用于表单变量
        for (FormProperty fp : formProperties) {
            String value = fp.getValue();
            String id = fp.getId();
            String name = fp.getName();
            log.info("value:{},id:{},name:{}", value, id, name);
        }
        return "success";
    }

    @GetMapping("/getTaskForm/{taskId}")
    public String getFormData(@PathVariable String taskId) {
        //查找表单变量
        FormInfo taskFormModel =  taskService.getTaskFormModel(taskId);
        SimpleFormModel formModel = (SimpleFormModel) taskFormModel.getFormModel();
        List<FormField> fields = formModel.getFields();
        fields.forEach(e->{
            System.out.println("e.getId() = " + e.getId());
            System.out.println("e.getName() = " + e.getName());
            System.out.println("e.getType() = " + e.getType());
            System.out.println("e.getValue() = " + e.getValue());

        });
        return "success";
    }
}
