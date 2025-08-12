package com.example.audit_lib_spring_boot_starter.kafka;

import com.example.audit_lib_spring_boot_starter.configs.AuditLibProperties;
import com.example.audit_lib_spring_boot_starter.kafka.dto.KafkaAnnotationLog;
import com.example.audit_lib_spring_boot_starter.kafka.dto.KafkaHttpLog;
import com.example.audit_lib_spring_boot_starter.utils.LogLevels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.example.audit_lib_spring_boot_starter.utils.LogLevels.OFF;

/**
 * Manages log writing to Kafka topic.
 */
@Component
public class KafkaLogger {

    private final LogLevels annotationLogLevel;

    private final LogLevels httpLogLevel;

    private final String topic;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaLogger(@Autowired AuditLibProperties properties) {
        annotationLogLevel = properties.getAnnotationKafkaLevel();
        httpLogLevel = properties.getHttpKafkaLevel();
        topic = properties.getKafkaTopicName();
    }

    /**
     * Sends message to Kafka topic from annotated methods.
     */
    public void log(String stage, String id, String methodName, String body) {
        if (!annotationLogLevel.equals(OFF)) {
            kafkaTemplate.send(topic,
                    new KafkaAnnotationLog(annotationLogLevel, stage, id, methodName, body));
        }
    }

    /**
     * Sends message to Kafka topic from http-requests.
     */
    public void log(String type, String method, int statusCode, String url, String request, String response) {
        if (!httpLogLevel.equals(OFF)) {
            kafkaTemplate.send(topic,
                    new KafkaHttpLog(type, method, statusCode, url, request, response));
        }
    }

}
