package com.example.audit_lib_spring_boot_starter.configs;

import com.example.audit_lib_spring_boot_starter.utils.LogLevels;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.example.audit_lib_spring_boot_starter.utils.LogLevels.INFO;
import static com.example.audit_lib_spring_boot_starter.utils.LogLevels.OFF;

@ConfigurationProperties(prefix = "audit-lib")
@Getter
@Setter
public class AuditLibProperties {

    /**
     * Defines logging level of HttpLogger (used in IncomingRequestFilter and OutgoingRequestInterceptor).
     */
    private LogLevels httpLoggingLevel = INFO;

    /**
     * Defines logging level of KafkaLogger (used in AnnotationLogAspect)
     */
    private LogLevels annotationKafkaLevel = OFF;

    /**
     * Defines logging level of KafkaLogger (used in IncomingRequestFilter and OutgoingRequestInterceptor).
     */
    private LogLevels httpKafkaLevel = OFF;

    private String kafkaTopicName = "AuditLib";

    private int kafkaPartitionNum = 1;

    private short kafkaReplicationFactor = 1;

}
