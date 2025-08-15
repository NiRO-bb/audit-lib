package com.example.audit_lib_spring_boot_starter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.audit_lib_spring_boot_starter.utils.LogLevels;

/**
 * Method-level annotation that provides opportunity to log method executing process.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {

    LogLevels logLevel();

}
