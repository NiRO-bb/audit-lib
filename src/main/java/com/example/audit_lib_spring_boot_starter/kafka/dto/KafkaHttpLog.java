package com.example.audit_lib_spring_boot_starter.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a JSON structure of log written in Kafka topic
 */
@Getter
@Setter
@NoArgsConstructor
public class KafkaHttpLog {

    @JsonIgnore
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private String date;

    private String type;

    private String method;

    private int statusCode;

    private String url;

    private String request;

    private String response;

    public KafkaHttpLog(String type, String method, int statusCode, String url, String request, String response) {
        date = LocalDateTime.now().format(formatter);
        this.type = type;
        this.method = method;
        this.statusCode = statusCode;
        this.url = url;
        this.request = request;
        this.response = response;
    }

}
