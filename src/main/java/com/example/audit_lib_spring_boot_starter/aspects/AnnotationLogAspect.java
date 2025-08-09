package com.example.audit_lib_spring_boot_starter.aspects;

import com.example.audit_lib_spring_boot_starter.kafka.KafkaLogger;
import com.example.audit_lib_spring_boot_starter.utils.LoggingUtil;
import com.example.audit_lib_spring_boot_starter.utils.LogLevels;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Represents the aspect for method logging.
 * Uses the @AuditLog annotation as pointcut and applies Around advice.
 */
@Aspect
@RequiredArgsConstructor
public class AnnotationLogAspect {

     private final Logger logger;
     private final KafkaLogger kafkaLogger;

    /**
     * Around advice.
     * Applies to methods annotated with @AuditLog annotation.
     * Logs some data about called method.
     */
    @Around("@annotation(com.example.audit_lib_spring_boot_starter.annotations.AuditLog)")
    public Object adviceAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        LogLevels logLevel = LoggingUtil.getAnnotationValue(joinPoint);
        Level level = LoggingUtil.getLevel(logLevel);

        String id = LoggingUtil.generateId();
        String methodName = LoggingUtil.getMethodName(joinPoint);
        String args = LoggingUtil.convertToString(joinPoint.getArgs());

        logger.log(level, "START {} {} args = {}", id, methodName, args);
        kafkaLogger.log("START", id, methodName, args);
        try {
            Object result = joinPoint.proceed();
            logger.log(level, "END {} {} result = {}", id, methodName, result);
            kafkaLogger.log("END", id, methodName, result);
            return result;
        } catch (Throwable t) {
            logger.log(level, "ERROR {} {} {}", id, methodName, t.getMessage());
            kafkaLogger.log("ERROR", id, methodName, t.getMessage());
            throw t;
        }
    }

}
