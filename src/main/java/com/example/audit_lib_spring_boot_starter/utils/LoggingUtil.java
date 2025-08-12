package com.example.audit_lib_spring_boot_starter.utils;

import com.example.audit_lib_spring_boot_starter.annotations.AuditLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
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
        return new String(body, StandardCharsets.UTF_8).replaceAll("[\\r\\n]", "");
    }

    /**
     * Converts Object instance to json string.
     *
     * @param object instance must be converted
     * @return json string
     * @throws IOException
     */
    public static String convertToString(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    /**
     * Converts Object array to json string.
     *
     * @param array array must be converted
     * @return
     */
    public static String convertToString(Object[] array) throws IOException {
        StringBuilder builder = new StringBuilder("[");
        for (Object obj : array) {
            builder.append(convertToString(obj)).append(", ");
        }
        if (builder.lastIndexOf(",") > 0) {
            builder.delete(builder.lastIndexOf(","), builder.length());
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * Creates URL as String type from passed parameters.
     *
     * @param path path without params
     * @param params name-value pairs
     * @return created URL
     */
    public static String getURL(String path, Map<String, String[]> params) {
        StringBuilder builder = new StringBuilder(path);
        if (!params.isEmpty()) {
            builder.append("?");
            for (String key : params.keySet()) {
                builder.append(String.format("%s=%s", key, Arrays.toString(params.get(key))));
                builder.append("&");
            }
            builder.deleteCharAt(builder.lastIndexOf("&"));
        }
        return builder.toString();
    }

}
