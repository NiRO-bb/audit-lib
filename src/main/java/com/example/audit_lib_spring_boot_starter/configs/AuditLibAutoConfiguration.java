package com.example.audit_lib_spring_boot_starter.configs;

import com.example.audit_lib_spring_boot_starter.aspects.AnnotationLogAspect;
import com.example.audit_lib_spring_boot_starter.interceptors.IncomingRequestFilter;
import com.example.audit_lib_spring_boot_starter.interceptors.OutgoingRequestInterceptor;
import com.example.audit_lib_spring_boot_starter.kafka.KafkaLogger;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides Beans for application.
 * Configures loggers for aspect and http-request interceptors.
 * Provides RestClient bean that can be overridden.
 */
@AutoConfiguration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(AuditLibProperties.class)
@RequiredArgsConstructor
public class AuditLibAutoConfiguration {

    private final AuditLibProperties properties;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    /**
     * Provides logger for Kafka.
     */
    @Bean
    public KafkaLogger kafkaLogger() {
        return new KafkaLogger(kafkaTemplate(),
                properties.getAnnotationKafkaLevel(),
                properties.getHttpKafkaLevel(),
                properties.getKafkaTopicName());
    }

    /**
     * Provides logger for annotated method logging.
     * Logger configuration defined in Log4j2.xml file.
     *
     * @return
     */
    @Bean("annotation")
    public Logger annotationLogger() {
        return LogManager.getLogger("AnnotationLogger");
    }

    /**
     * Provides logger for http-request logging.
     * Logger configuration defined in Log4j2.xml file.
     *
     * @return
     */
    @Bean("http")
    public Logger httpLogger() {
        return LogManager.getLogger("HttpLogger");
    }

    /**
     * Provides aspect for annotated method logging.
     *
     * @return
     */
    @Bean
    public AnnotationLogAspect annotationLogAspect() {
        return new AnnotationLogAspect(annotationLogger(), kafkaLogger());
    }

    /**
     * Provides RestClient instance that must be used for request logging.
     * Uses OutgoingRequestInterceptor bean for logging.
     *
     * @return RestClient instance with logging ability
     */
    @Bean
    @ConditionalOnMissingBean(RestClient.class)
    public RestClient restClient() {
        return RestClient.builder()
                .requestInterceptor(outgoingRequestInterceptor())
                .build();
    }

    /**
     * Provides bean (implementing Filter interface) for logging incoming http-requests.
     *
     * @return
     */
    @Bean
    public IncomingRequestFilter incomingRequestFilter() {
        return new IncomingRequestFilter(httpLogger(), kafkaLogger(), properties.getHttpLoggingLevel());
    }

    /**
     * Provides bean (implementing ClientHttpRequestInterceptor interface) for logging outgoing http-requests.
     *
     * @return
     */
    @Bean
    public OutgoingRequestInterceptor outgoingRequestInterceptor() {
        return new OutgoingRequestInterceptor(httpLogger(), kafkaLogger(), properties.getHttpLoggingLevel());
    }


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
     * Provides new Kafka topic.
     *
     * @return
     */
    @Bean
    public NewTopic topic() {
        return new NewTopic(properties.getKafkaTopicName(),
                properties.getKafkaPartitionNum(),
                properties.getKafkaReplicationFactor());
    }

    /**
     * Creates ProducerFactory instance.
     * Sets some settings:
     * StringSerializer for key, JsonSerializer for value, enable idempotence.
     * Uses JsonSerializer type mapping:
     * 'http' for KafkaHttpLog class, 'annotation' for KafkaAnnotationLog class.
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
                "http:com.example.audit_lib_spring_boot_starter.kafka.dto.KafkaHttpLog, " +
                        "annotation:com.example.audit_lib_spring_boot_starter.kafka.dto.KafkaAnnotationLog");
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
