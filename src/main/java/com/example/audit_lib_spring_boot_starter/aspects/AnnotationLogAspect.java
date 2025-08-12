package com.example.audit_lib_spring_boot_starter.aspects;

import com.example.audit_lib_spring_boot_starter.kafka.KafkaLogger;
import com.example.audit_lib_spring_boot_starter.utils.LoggingUtil;
import com.example.audit_lib_spring_boot_starter.utils.LogLevels;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Represents the aspect for method logging.
 * Uses the @AuditLog annotation as pointcut and applies Around advice.
 */
@Aspect
@Component
public class AnnotationLogAspect {

     private final Logger logger = LogManager.getLogger("AnnotationLogger");

     @Autowired
     private KafkaLogger kafkaLogger;

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
            String jsonResult = LoggingUtil.convertToString(result);
            logger.log(level, "END {} {} result = {}", id, methodName, jsonResult);
            kafkaLogger.log("END", id, methodName, jsonResult);
            return result;
        } catch (Throwable t) {
            logger.log(level, "ERROR {} {} {}", id, methodName, t.getMessage());
            kafkaLogger.log("ERROR", id, methodName, t.getMessage());
            throw t;
        }
    }

}
