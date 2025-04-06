package com.bpm.example.demo2.listener;

import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.context.Context;

import java.util.List;
import java.util.Map;

public class ListenerUtil {
    //设置命名空间
    private static String namespace = "http://flowable.org/bpmn";
    //获取流程引擎配置
    protected ProcessEngineConfigurationImpl procEngineConf = Context
            .getProcessEngineConfiguration();
    //获取表达式管理器
    protected ExpressionManager expressionManager = procEngineConf.getExpressionManager();

    /**
     * 查询ExtensionElement定义的扩展属性的内容
     *
     * @param element              流程元素对象
     * @param extensionElementName 扩展属性名
     * @return
     */
    public String getExtensionElementValue(BaseElement element, String extensionElementName) {
        Map<String, List<ExtensionElement>> extensionElementMap = element
                .getExtensionElements();
        List<ExtensionElement> extensionElements = extensionElementMap.get
                (extensionElementName);
        if (extensionElements != null && !extensionElements.isEmpty()) {
            for (ExtensionElement extensionElement : extensionElements) {
                if ((namespace == null && extensionElement.getNamespace() == null) ||
                        namespace.equals(extensionElement.getNamespace())) {
                    return extensionElement.getElementText();
                }
            }
        }
        return null;
    }

    /**
     * 查询ExtensionAttribute定义的扩展属性的内容
     *
     * @param element              流程元素对象
     * @param extensionElementName 扩展属性名
     * @return
     */
    public String getExtensionAttributeValue(BaseElement element, String
            extensionElementName) {
        String extensionAttribute = element.getAttributeValue(namespace, extensionElementName);
        return extensionAttribute;
    }

    /**
     * 计算表达式的值
     *
     * @param express           表达式文本
     * @param variableContainer 变量所在的容器
     * @return
     */
    public String getExpressValue(String express, VariableContainer variableContainer) {
        return (String) expressionManager.createExpression(express).getValue
                (variableContainer);
    }
}
