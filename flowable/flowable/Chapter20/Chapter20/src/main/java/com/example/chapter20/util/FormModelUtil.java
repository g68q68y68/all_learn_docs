package com.example.chapter20.util;

import com.example.chapter20.domain.Model;
import com.example.chapter20.engine.form.model.CustomFormModel;
import com.example.chapter20.engine.form.model.CustomFormModel.CustomFormField;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.form.model.FormField;
import org.flowable.form.model.LayoutDefinition;
import org.flowable.form.model.SimpleFormModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FormModelUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Model转CustomFormModel
     */
    public static CustomFormModel trans(Model formModel) throws Exception {
        CustomFormModel customFormModel = new CustomFormModel();
        customFormModel.setName(formModel.getName());
        customFormModel.setKey(formModel.getKey());
        customFormModel.setDescription(formModel.getDescription());
        customFormModel.setVersion(formModel.getVersion());
        String modelEditorJson = formModel.getModelEditorJson();
        Map<String, CustomFormField> fieldMap =
                objectMapper.readValue(modelEditorJson, new TypeReference<Map<String, CustomFormField>>() {
                });
        customFormModel.setFields(fieldMap);
        return customFormModel;
    }

    /**
     * CustomFormModel转SimpleFormModel
     */
    public static SimpleFormModel trans(CustomFormModel formModel) throws Exception {
        SimpleFormModel simpleFormModel = new SimpleFormModel();
        simpleFormModel.setName(formModel.getName());
        simpleFormModel.setKey(formModel.getKey());
        simpleFormModel.setDescription(formModel.getDescription());
        simpleFormModel.setVersion(formModel.getVersion());
        simpleFormModel.setFields(convertFormFields(formModel.getFields()));
        return simpleFormModel;
    }

    /**
     * SimpleFormModel转CustomFormModel
     */
    public static CustomFormModel trans(SimpleFormModel simpleFormModel) throws Exception {
        CustomFormModel customFormModel = new CustomFormModel();
        customFormModel.setName(simpleFormModel.getName());
        customFormModel.setKey(simpleFormModel.getKey());
        customFormModel.setDescription(simpleFormModel.getDescription());
        customFormModel.setVersion(simpleFormModel.getVersion());

        List<FormField> formFields = simpleFormModel.getFields();
        Map<String, CustomFormField> fieldMap = new LinkedHashMap<>();
        for (FormField formField : formFields) {
            String fieldId = formField.getId();
            CustomFormField fieldObj = convertFormField(formField);
            fieldMap.put(fieldId, fieldObj);
        }
        customFormModel.setFields(fieldMap);
        return customFormModel;
    }

    /**
     * CustomFormField转FormField
     */
    private static List<FormField> convertFormFields(Map<String, CustomFormField> fieldMap) throws Exception {
        List<FormField> formFields = new ArrayList<>();
        if (fieldMap != null) {
            int rowNum = 1;
            for (Map.Entry<String, CustomFormField> fieldEntry : fieldMap.entrySet()) {
                String fieldId = fieldEntry.getKey();
                CustomFormField fieldObj = fieldEntry.getValue();
                FormField formField = convertFormField(fieldId, fieldObj);
                formField.setLayout(new LayoutDefinition(rowNum));
                formFields.add(formField);
                rowNum++;
            }
        }
        return formFields;
    }

    private static FormField convertFormField(String fieldId, CustomFormField customFormField) throws Exception {
        FormField formField = new FormField();
        formField.setId(fieldId);
        formField.setName(customFormField.getTitle());
        formField.setType(customFormField.getWidget());
        formField.setRequired(customFormField.isRequired());
        formField.setReadOnly(customFormField.isReadOnly());

        if (customFormField.getProps() != null) {
            if (customFormField.getProps().getPlaceholder() != null) {
                String placeholder = objectMapper.writeValueAsString(customFormField.getProps().getPlaceholder());
                formField.setPlaceholder(placeholder);
            }
//            formField.setPlaceholder(customFormField.getProps().getPlaceholder().toString());

            List<CustomFormModel.Option> options = customFormField.getProps().getOptions();
            if (options != null && options.size() > 0) {
                Map<String, Object> params = new HashMap<>();
                params.put("options", options);
                formField.setParams(params);
            }
        }
        return formField;
    }

    /**
     * FormField转CustomFormField
     */
    private static CustomFormField convertFormField(FormField formField) throws Exception {
        CustomFormField customFormField = new CustomFormField();
        customFormField.setTitle(formField.getName());
        customFormField.setWidget(formField.getType());
        customFormField.setRequired(formField.isRequired());
        customFormField.setReadOnly(formField.isReadOnly());
        customFormField.setValue(formField.getValue());
        CustomFormModel.Props props = new CustomFormModel.Props();
        if (formField.getPlaceholder() != null) {
            Object placeholder = objectMapper.readValue(formField.getPlaceholder(), Object.class);
            props.setPlaceholder(placeholder);
        }

        formField.getValue();
        Object options = formField.getParam("options");
        if (options instanceof List) {
            props.setOptions((List) options);
        }
        customFormField.setProps(props);
        return customFormField;
    }


}
