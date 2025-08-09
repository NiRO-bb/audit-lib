package com.example.audit_lib_spring_boot_starter.kafka;

import com.example.audit_lib_spring_boot_starter.utils.LogLevels;
import com.example.audit_lib_spring_boot_starter.kafka.dto.KafkaAnnotationLog;
import com.example.audit_lib_spring_boot_starter.kafka.dto.KafkaHttpLog;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

import static com.example.audit_lib_spring_boot_starter.utils.LogLevels.OFF;

/**
 * Manages log writing to Kafka topic.
 */
@RequiredArgsConstructor
public class KafkaLogger {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final LogLevels annotationLogLevel;

    private final LogLevels httpLogLevel;

    private final String topicName;

    /**
     * Sends message to Kafka topic from annotated methods.
     */
    public void log(String stage, String id, String methodName, Object body) {
        if (!annotationLogLevel.equals(OFF)) {
            kafkaTemplate.send(topicName,
                    new KafkaAnnotationLog(annotationLogLevel, stage, id, methodName, body.toString()));
        }
    }

    /**
     * Sends message to Kafka topic from http-requests.
     */
    public void log(String type, String method, int statusCode, String url, String request, String response) {
        if (!httpLogLevel.equals(OFF)) {
            kafkaTemplate.send(topicName,
                    new KafkaHttpLog(type, method, statusCode, url, request, response));
        }
    }

}
