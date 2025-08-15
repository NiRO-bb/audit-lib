package com.example.audit_lib_spring_boot_starter.interceptors.wrappers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a wrapper for http-response.
 * Gives opportunity to read response body many times.
 */
public class OutgoingResponseWrapper implements ClientHttpResponse {

    private final ClientHttpResponse response;
    private byte[] data;

    public OutgoingResponseWrapper(ClientHttpResponse response) throws IOException {
        this.response = response;
        InputStream inputStream = response.getBody();
        this.data = inputStream.readAllBytes();
    }

    @Override
    public HttpStatusCode getStatusCode() throws IOException {
        return response.getStatusCode();
    }

    @Override
    public String getStatusText() throws IOException {
        return response.getStatusText();
    }

    @Override
    public void close() {
        response.close();
    }

    @Override
    public InputStream getBody() {
        return new ByteArrayInputStream(data);
    }

    @Override
    public HttpHeaders getHeaders() {
        return response.getHeaders();
    }

}
