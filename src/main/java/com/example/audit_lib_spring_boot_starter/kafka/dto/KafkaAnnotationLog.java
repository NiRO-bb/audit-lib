package com.example.audit_lib_spring_boot_starter.kafka.dto;

import com.example.audit_lib_spring_boot_starter.utils.LogLevels;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a JSON structure of log written in Kafka topic
 */
@Getter
@Setter
public class KafkaAnnotationLog {

    @JsonIgnore
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private String date;

    private LogLevels logLevel;

    private String stage;

    private String id;

    private String methodName;

    private String body;

    public KafkaAnnotationLog(LogLevels logLevel, String stage, String id, String methodName, String body) {
        date = LocalDateTime.now().format(formatter);
        this.logLevel = logLevel;
        this.stage = stage;
        this.id = id;
        this.methodName = methodName;
        this.body = body;
    }

}
