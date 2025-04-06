package com.bpm.example.servicetask.demo1.bean;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.flowable.engine.delegate.DelegateExecution;

import java.io.Serializable;

@Data
public class BusinessBean implements Serializable {
    private float total;

    /**
     * 无参表达式
     */
    public void calculateMount1() {
        //此处省略方法逻辑代码
    }

    /**
     * 带参数的表达式
     * @param execution   DelegateExecution对象
     * @param money       名称为money的流程变量
     */
    public void calculateMount2(DelegateExecution execution, float money) {
        //此处省略方法逻辑代码
    }
}
