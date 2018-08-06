package com.jay.spring.aop.aspectj;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author xiang.wei
 * @create 2018/8/6 15:58
 */
public class AspectJBeforeAdvice extends AbstractAspectJAdvice {

    public AspectJBeforeAdvice(Method adviceMethod, AspectJExpressionPointcut pointcut, Object adviceObject) {
        super(adviceMethod, pointcut, adviceObject);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
//        例如：调用TransactionManager的start方法
        this.invokeAdviceMethod();
        Object proceed = methodInvocation.proceed();
        return proceed;
    }
}
