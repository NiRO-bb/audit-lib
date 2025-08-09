package com.example.audit_lib_spring_boot_starter.utils;

import com.example.audit_lib_spring_boot_starter.annotations.AuditLog;
import org.apache.logging.log4j.Level;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Contains some methods for AnnotationLogAspect.
 * Helps to process advised methods.
 */
public final class LoggingUtil {

    private LoggingUtil() {}

    /**
     * Retrieves value from annotation.
     *
     * @param joinPoint called advised method
     * @return LogLevels instance
     */
    public static LogLevels getAnnotationValue(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AuditLog annotation = method.getAnnotation(AuditLog.class);
        return annotation.logLevel();
    }

    /**
     * Casts LogLevels instance to Level instance.
     *
     * @param logLevel
     * @return one of logging level
     */
    public static Level getLevel(LogLevels logLevel) {
        return switch (logLevel) {
            case OFF -> Level.OFF;
            case FATAL -> Level.FATAL;
            case ERROR -> Level.ERROR;
            case WARN -> Level.WARN;
            case INFO -> Level.INFO;
            case DEBUG -> Level.DEBUG;
            case TRACE -> Level.TRACE;
            case ALL -> Level.ALL;
        };
    }

    /**
     * Retrieves name of called method and name of class declaring this method.
     *
     * @return name with template "ClassName.MethodName"
     */
    public static String getMethodName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getMethod().getName();
        return String.format("%s.%s", className, methodName);
    }

    /**
     * Retrieves random id value.
     * Uses UUID random generator.
     *
     * @return generated id value
     */
    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Casts body from byte array to String type.
     *
     * @param body data must be converted
     * @return 'body' as String
     */
    public static String getBody(byte[] body) {
        if (body.length == 0) {
            return "{}";
        }
        return new String(body, StandardCharsets.UTF_8);
    }

    /**
     * Converts Object array to string.
     *
     * @param array array must be converted to String type
     * @return resulting string - [...]
     */
    public static String convertToString(Object[] array) {
        StringBuilder builder = new StringBuilder("[");
        for (Object o : array) {
            builder.append(o.toString()).append(", ");
        }
        if (builder.lastIndexOf(",") > 0) {
            builder.delete(builder.lastIndexOf(","), builder.length() - 1);
        }
        builder.append("]");
        return builder.toString();
    }

}
