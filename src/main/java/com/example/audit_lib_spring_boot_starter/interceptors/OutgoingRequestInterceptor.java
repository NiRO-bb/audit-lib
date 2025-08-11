package com.example.audit_lib_spring_boot_starter.interceptors;

import com.example.audit_lib_spring_boot_starter.configs.AuditLibProperties;
import com.example.audit_lib_spring_boot_starter.interceptors.wrappers.OutgoingResponseWrapper;
import com.example.audit_lib_spring_boot_starter.kafka.KafkaLogger;
import com.example.audit_lib_spring_boot_starter.utils.LogLevels;
import com.example.audit_lib_spring_boot_starter.utils.LoggingUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Intercepts outgoing http-requests and logs them.
 */
@Component
public class OutgoingRequestInterceptor implements ClientHttpRequestInterceptor {

    private final Logger logger = LogManager.getLogger("HttpLogger");

    private final LogLevels httpLoggingLevel;

    @Autowired
    private KafkaLogger kafkaLogger;

    public OutgoingRequestInterceptor(@Autowired AuditLibProperties properties) {
        httpLoggingLevel = properties.getHttpLoggingLevel();
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        OutgoingResponseWrapper responseWrapper = new OutgoingResponseWrapper(response);

        String method = request.getMethod().toString();
        int statusCode = Integer.valueOf(response.getStatusCode().toString().split(" ")[0]);
        String url = request.getURI().toString();
        String requestBody = LoggingUtil.getBody(body);
        String responseBody = LoggingUtil.getBody(responseWrapper.getBody().readAllBytes());

        logger.log(LoggingUtil.getLevel(httpLoggingLevel), "Outgoing {} {} {} RequestBody = {} ResponseBody = {}",
                method, statusCode, url, requestBody, responseBody);
        kafkaLogger.log("Outgoing", method, statusCode, url, requestBody, responseBody);
        return responseWrapper;
    }

}
