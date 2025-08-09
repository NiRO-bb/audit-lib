package com.example.audit_lib_spring_boot_starter.interceptors;

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
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import jakarta.servlet.Filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * Intercepts incoming http-requests and logs them.
 */
@RequiredArgsConstructor
public class IncomingRequestFilter implements Filter {

    private final Logger logger;

    private final KafkaLogger kafkaLogger;

    private final LogLevels logLevel;

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
        String url = getURL(request.getServletPath(), servletRequest.getParameterMap());

        logger.log(LoggingUtil.getLevel(logLevel), "Incoming {} {} {} RequestBody = {} ResponseBody = {}",
                method, statusCode, url, requestData, responseData);
        kafkaLogger.log("Incoming", method, statusCode, url, requestData, responseData);
        servletResponse.getOutputStream().write(responseAsByte);
    }

    /**
     * Creates URL as String type from passed parameters.
     *
     * @param path path without params
     * @param params name-value pairs
     * @return created URL
     */
    private String getURL(String path, Map<String, String[]> params) {
        StringBuilder builder = new StringBuilder(path);
        if (!params.isEmpty()) {
            builder.append("?");
            for (String key : params.keySet()) {
                builder.append(String.format("%s=%s", key, Arrays.toString(params.get(key))));
                builder.append("&");
            }
            builder.deleteCharAt(builder.lastIndexOf("&"));
        }
        return builder.toString();
    }

}
