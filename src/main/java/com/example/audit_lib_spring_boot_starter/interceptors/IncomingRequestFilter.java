package com.example.audit_lib_spring_boot_starter.interceptors;

import com.example.audit_lib_spring_boot_starter.configs.AuditLibProperties;
import com.example.audit_lib_spring_boot_starter.interceptors.wrappers.IncomingRequestWrapper;
import com.example.audit_lib_spring_boot_starter.interceptors.wrappers.IncomingResponseWrapper;
import com.example.audit_lib_spring_boot_starter.kafka.KafkaLogger;
import com.example.audit_lib_spring_boot_starter.utils.LogLevels;
import com.example.audit_lib_spring_boot_starter.utils.LoggingUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Intercepts incoming http-requests and logs them.
 */
@Component
public class IncomingRequestFilter implements Filter {

    private final Logger logger = LogManager.getLogger("HttpLogger");

    private final LogLevels httpLoggingLevel;

    @Autowired
    private KafkaLogger kafkaLogger;

    public IncomingRequestFilter(@Autowired AuditLibProperties properties) {
        httpLoggingLevel = properties.getHttpLoggingLevel();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        IncomingRequestWrapper requestWrapper = new IncomingRequestWrapper(request);
        IncomingResponseWrapper responseWrapper = new IncomingResponseWrapper(response);
        filterChain.doFilter(requestWrapper, responseWrapper);

        String requestData = LoggingUtil.getBody(requestWrapper.getInputStream().readAllBytes());
        byte[] responseAsByte = responseWrapper.getResponseAsByte();
        String responseData = LoggingUtil.getBody(responseAsByte);

        String method = request.getMethod();
        int statusCode = response.getStatus();
        String url = LoggingUtil.getURL(request.getServletPath(), servletRequest.getParameterMap());

        logger.log(LoggingUtil.getLevel(httpLoggingLevel), "Incoming {} {} {} RequestBody = {} ResponseBody = {}",
                method, statusCode, url, requestData, responseData);
        kafkaLogger.log("Incoming", method, statusCode, url, requestData, responseData);
        servletResponse.getOutputStream().write(responseAsByte);
    }

}
