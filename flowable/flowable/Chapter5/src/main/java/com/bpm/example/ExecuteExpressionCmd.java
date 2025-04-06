package com.bpm.example;

import org.flowable.common.engine.impl.de.odysseus.el.ExpressionFactoryImpl;
import org.flowable.common.engine.impl.de.odysseus.el.util.SimpleContext;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.javax.el.ExpressionFactory;
import org.flowable.common.engine.impl.javax.el.ValueExpression;

import java.util.Map;

public class ExecuteExpressionCmd implements Command<Object> {
    //表达式
    private String express;
    //变量
    private Map<String, Object> variableMap;

    public ExecuteExpressionCmd(String express, Map<String, Object> variableMap) {
        this.express = express;
        this.variableMap = variableMap;
    }

    @Override
    public Object execute(CommandContext commandContext) {
        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        if (variableMap != null) {
            for (String key : variableMap.keySet()) {
                if (variableMap.get(key) != null) {
                    context.setVariable(key,
                            factory.createValueExpression(variableMap.get(key),
                                    variableMap.get(key).getClass()));
                } else {
                    context.setVariable(key,
                            factory.createValueExpression(null, Object.class));
                }
            }
        }
        ValueExpression valueExpression = factory.createValueExpression(context,
                express, Object.class);
        return valueExpression.getValue(context);
    }
}

