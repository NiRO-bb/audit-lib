package com.example.audit_lib_spring_boot_starter.interceptors;

import com.example.audit_lib_spring_boot_starter.interceptors.wrappers.OutgoingResponseWrapper;
import com.example.audit_lib_spring_boot_starter.kafka.KafkaLogger;
import com.example.audit_lib_spring_boot_starter.utils.LogLevels;
import com.example.audit_lib_spring_boot_starter.utils.LoggingUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Intercepts outgoing http-requests and logs them.
 */
@RequiredArgsConstructor
public class OutgoingRequestInterceptor implements ClientHttpRequestInterceptor {

    private final Logger logger;

    private final KafkaLogger kafkaLogger;

    private final LogLevels logLevel;

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

        logger.log(LoggingUtil.getLevel(logLevel), "Outgoing {} {} {} RequestBody = {} ResponseBody = {}",
                method, statusCode, url, requestBody, responseBody);
        kafkaLogger.log("Outgoing", method, statusCode, url, requestBody, responseBody);
        return responseWrapper;
    }

}
