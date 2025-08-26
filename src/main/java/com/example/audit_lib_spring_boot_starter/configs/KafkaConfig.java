package com.example.audit_lib_spring_boot_starter.configs;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides Beans for Kafka configuration.
 * Configures Kafka producer.
 */
@AutoConfiguration
@EnableConfigurationProperties(AuditLibProperties.class)
@RequiredArgsConstructor
public class KafkaConfig {

    private final AuditLibProperties properties;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    /**
     * Provides bean responsible for adding new Kafka topics.
     *
     * @return
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    /**
     * Creates new Kafka topic for annotated method logs.
     *
     * @return created Kafka topic
     */
    @Bean
    public NewTopic methodTopic() {
        return new NewTopic(properties.getKafkaMethodsTopic(),
                properties.getKafkaPartitionNum(),
                properties.getKafkaReplicationFactor());
    }

    /**
     * Creates new Kafka topic for http-request logs.
     *
     * @return created Kafka topic
     */
    @Bean
    public NewTopic requestTopic() {
        return new NewTopic(properties.getKafkaRequestsTopic(),
                properties.getKafkaPartitionNum(),
                properties.getKafkaReplicationFactor());
    }


    /**
     * Creates ProducerFactory instance.
     * Sets some settings:
     * StringSerializer for key, JsonSerializer for value, enable idempotence.
     * Uses JsonSerializer type mapping:
     * 'http' for KafkaRequestLog class, 'annotation' for KafkaMethodLog class.
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ProducerFactory.class)
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configs.put(JsonSerializer.TYPE_MAPPINGS,
                "http:com.example.audit_lib_spring_boot_starter.kafka.dto.KafkaRequestLog, " +
                        "annotation:com.example.audit_lib_spring_boot_starter.kafka.dto.KafkaMethodLog");
        configs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configs.put(ProducerConfig.ACKS_CONFIG, "all");
        return new DefaultKafkaProducerFactory<>(configs);
    }

    /**
     * Creates KafkaTemplate for message sending.
     *
     * @return
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
