package com.example.chapter20.engine.form.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.flowable.form.api.FormModel;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CustomFormModel implements FormModel {
    private static final long serialVersionUID = 1L;
    protected String name;
    protected String key;
    protected int version;
    protected String description;
    protected Map<String, CustomFormField> fields;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CustomFormField {
        String widget; //组件类型
        String title; //组件标签
        String type; //组件值类型
        Object value; //组件值
        String format; //组件值格式
        boolean readOnly; //只读
        boolean required; //必填
        String readOnlyWidget; //只读组件
        Props props; //组件属性
        String maxWidth; //组件宽度
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Props {
        List<Option> options;  //选项列表
        Object placeholder; //占位提示
        boolean allowClear;
        boolean showCount;
        boolean autoSize;
    }

    @Data
    public static class Option {
        String label; //选项名
        String value; //选项值
    }
}
