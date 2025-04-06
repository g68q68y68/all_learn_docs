package com.bpm.example.demo1.http.behavior;

import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.flowable.http.common.impl.ExpressionUtils;

@Slf4j
public class CustomHttpServiceActivityBehavior extends AbstractBpmnActivityBehavior {

    // HttpRequest method (GET,POST,PUT etc)
    protected Expression requestMethod;
    // HttpRequest URL (http://flowable.org)
    protected Expression requestUrl;
    // Line separated HTTP body headers(Optional)
    protected Expression requestHeaders;
    // HttpRequest body expression (Optional)
    protected Expression requestBody;
    // HttpRequest body encoding expression, for example UTF-8 (Optional)
    protected Expression requestBodyEncoding;
    // Timeout in seconds for the body (Optional)
    protected Expression requestTimeout;
    // HttpRequest retry disable HTTP redirects (Optional)
    protected Expression disallowRedirects;
    // Comma separated list of HTTP body status codes to fail, for example 400,5XX (Optional)
    protected Expression failStatusCodes;
    // Comma separated list of HTTP body status codes to handle, for example 404,3XX (Optional)
    protected Expression handleStatusCodes;
    // Flag to ignore exceptions (Optional)
    protected Expression ignoreException;
    // Flag to save request variables. Default is false (Optional)
    protected Expression saveRequestVariables;
    // Flag to save response variables. Default is false (Optional)
    protected Expression saveResponseParameters;
    // Variable name for response body
    protected Expression responseVariableName;
    // Flag to save the response variables as a transient variable. Default is false (Optional).
    protected Expression saveResponseParametersTransient;
    // Flag to save the response variable as an ObjectNode instead of a String
    protected Expression saveResponseVariableAsJson;
    // Prefix for the execution variable names (Optional)
    protected Expression resultVariablePrefix;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("进入自定义HttpServiceActivityBehavior");
        log.info("requestMethod:{}", ExpressionUtils.getStringFromField(requestMethod, execution));
        log.info("requestUrl:{}", ExpressionUtils.getStringFromField(requestUrl, execution));
        log.info("requestBody:{}", ExpressionUtils.getStringFromField(requestBody, execution));
        log.info("requestBodyEncoding:{}", ExpressionUtils.getStringFromField(requestBodyEncoding, execution));
        log.info("requestTimeout:{}", ExpressionUtils.getIntFromField(requestTimeout, execution));
        log.info("disallowRedirects:{}", ExpressionUtils.getBooleanFromField(disallowRedirects, execution));
        log.info("ignoreException:{}", ExpressionUtils.getBooleanFromField(ignoreException, execution));
        log.info("saveRequestVariables:{}", ExpressionUtils.getBooleanFromField(saveRequestVariables, execution));
        log.info("saveResponseParameters:{}", ExpressionUtils.getBooleanFromField(saveResponseParameters, execution));
        log.info("saveResponseParametersTransient:{}", ExpressionUtils.getBooleanFromField(saveResponseParametersTransient, execution));
        log.info("saveResponseVariableAsJson:{}", ExpressionUtils.getBooleanFromField(saveResponseVariableAsJson, execution));
        log.info("resultVariablePrefix:{}", ExpressionUtils.getStringFromField(resultVariablePrefix, execution));
        log.info("failStatusCodes:{}", ExpressionUtils.getStringFromField(failStatusCodes, execution));
        log.info("handleStatusCodes:{}", ExpressionUtils.getStringFromField(handleStatusCodes, execution));
        log.info("实际调用外部HTTP服务请自行实现");
    }


}
