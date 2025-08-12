package com.example.audit_lib_spring_boot_starter.configs;

import com.example.audit_lib_spring_boot_starter.utils.LogLevels;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.example.audit_lib_spring_boot_starter.utils.LogLevels.INFO;

@ConfigurationProperties(prefix = "audit-lib")
@Getter
@Setter
public class AuditLibProperties {

    /**
     * Defines logging level of HttpLogger (used in IncomingRequestFilter and OutgoingRequestInterceptor).
     */
    private LogLevels httpLoggingLevel = INFO;

    private String kafkaTopicName = "AuditLib";

    private int kafkaPartitionNum = 1;

    private short kafkaReplicationFactor = 1;

}
