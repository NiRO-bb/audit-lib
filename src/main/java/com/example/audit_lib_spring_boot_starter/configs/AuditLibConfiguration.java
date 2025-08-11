package com.example.audit_lib_spring_boot_starter.configs;

import com.example.audit_lib_spring_boot_starter.interceptors.OutgoingRequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

/**
 * Provides Beans for application.
 * Configures loggers for aspect and http-request interceptors.
 * Provides RestClient bean that can be overridden.
 */
@AutoConfiguration
@EnableConfigurationProperties(AuditLibProperties.class)
@RequiredArgsConstructor
public class AuditLibConfiguration {

    private final AuditLibProperties properties;

    @Autowired
    private OutgoingRequestInterceptor outgoingRequestInterceptor;

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
                .requestInterceptor(outgoingRequestInterceptor)
                .build();
    }

}
