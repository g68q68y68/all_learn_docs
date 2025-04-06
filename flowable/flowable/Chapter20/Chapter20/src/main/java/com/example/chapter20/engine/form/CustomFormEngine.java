package com.example.chapter20.engine.form;

import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.engine.form.FormData;
import org.flowable.engine.form.StartFormData;
import org.flowable.engine.form.TaskFormData;
import org.flowable.engine.impl.form.FormEngine;
import org.flowable.engine.impl.persistence.entity.ResourceEntity;
import org.flowable.engine.impl.util.CommandContextUtil;

import java.nio.charset.StandardCharsets;

public class CustomFormEngine implements FormEngine {
    @Override
    public String getName() {
        return "custom-form-engine";
    }

    @Override
    public Object renderStartForm(StartFormData startForm) {
        if (startForm.getFormKey() == null) {
            return null;
        }
        return getFormTemplateString(startForm, startForm.getFormKey());
    }

    @Override
    public Object renderTaskForm(TaskFormData taskForm) {
        if (taskForm.getFormKey() == null) {
            return null;
        }
        return getFormTemplateString(taskForm, taskForm.getFormKey());
    }

    protected String getFormTemplateString(FormData formInstance, String formKey) {
        String deploymentId = formInstance.getDeploymentId();

        ResourceEntity resourceStream = CommandContextUtil.getResourceEntityManager()
                .findResourceByDeploymentIdAndResourceName(deploymentId, getResourceName(formKey));

        if (resourceStream == null) {
            throw new FlowableObjectNotFoundException("Form with formKey '" + formKey + "' does not exist",
                    String.class);
        }

        return new String(resourceStream.getBytes(), StandardCharsets.UTF_8);
    }

    protected String getResourceName(String formKey) {
        return "form-" + formKey + ".form";
    }
}
