package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutofillAspect {

    /**
     * 切入点表达式，对mapper接口中添加了AutoFill注解的方法进行拦截
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {}

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws Exception {
        log.info("开始进行数据填充");
        // 获取当前方法签名
        Signature signature = joinPoint.getSignature();
        // 强转为方法签名对象
        MethodSignature methodSignature = (MethodSignature) signature;
        // 获取当前方法注解
        AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class);
        // 获取当前注解中数据库操作类型
        OperationType value = autoFill.value();

        // 获取方法参数的对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object param = args[0];

        // 准备赋值数据
        Long currentId = BaseContext.getCurrentId();
        LocalDateTime now = LocalDateTime.now();

        // 根据对应的操作类型，为对应的属性赋值
        if (value == OperationType.INSERT) {
            param.getClass().getMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class).invoke(param, now);
            param.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class).invoke(param, now);
            param.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class).invoke(param, currentId);
            param.getClass().getMethod(AutoFillConstant.SET_CREATE_USER, Long.class).invoke(param, currentId);
        }else if (value == OperationType.UPDATE) {
            param.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class).invoke(param, now);
            param.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER , Long.class).invoke(param, currentId);

        }
    }
}
