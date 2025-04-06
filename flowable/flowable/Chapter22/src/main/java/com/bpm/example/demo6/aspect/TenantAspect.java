package com.bpm.example.demo6.aspect;

import com.bpm.example.demo6.annotation.TenantAnnotation;
import com.bpm.example.demo6.multidatasource.MultiTenantInfoHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

@Slf4j
public class TenantAspect {
    @Autowired
    private MultiTenantInfoHolder multiTenantInfoHolder;

    //定义
    public Object tenantPointCut(ProceedingJoinPoint point) throws Throwable {
        //获取方法签名
        MethodSignature signature = (MethodSignature) point.getSignature();
        //获取切入方法的对象
        Method method = signature.getMethod();
        //获取方法上的Aop注解
        TenantAnnotation ds = method.getAnnotation(TenantAnnotation.class);
        //获取注解上的tenantId值
        String tenantId = generateKeyBySpEL(ds.tenantId(), point);
        log.info("切换到业务方{}开始", tenantId);
        //切换租户
        multiTenantInfoHolder.setCurrentTenantId(tenantId);
        try {
            return point.proceed();
        } finally {
            //清空租户
            multiTenantInfoHolder.clearCurrentTenantId();
            log.info("切换到业务方{}结束", tenantId);
        }
    }

    //使用 SpelExpressionParser 和DefaultParameterNameDiscoverer 来动态获取参数
    private String generateKeyBySpEL(String key, ProceedingJoinPoint pjp) {
        SpelExpressionParser parserSpel = new SpelExpressionParser();
        DefaultParameterNameDiscoverer parameterNameDiscoverer
                = new DefaultParameterNameDiscoverer();
        Expression expression = parserSpel.parseExpression(key);
        EvaluationContext context = new StandardEvaluationContext();
        //获取方法的参数名和参数值
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Object[] args = pjp.getArgs();
        String[] paramNames = parameterNameDiscoverer
                .getParameterNames(methodSignature.getMethod());
        //将方法的参数名和参数值一一对应的放入上下文中
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        //解析表达式获取值
        return expression.getValue(context).toString();
    }
}
