package com.example.audit_lib_spring_boot_starter.interceptors.wrappers;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Represents a wrapper for http-request.
 * Gives opportunity to read request body many times.
 */
public class IncomingRequestWrapper extends HttpServletRequestWrapper {

    private byte[] data;

    public IncomingRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        InputStream inputStream = request.getInputStream();
        data = StreamUtils.copyToByteArray(inputStream);
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return inputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {}

            @Override
            public int read() {
                return inputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        return new BufferedReader(new InputStreamReader(inputStream));
    }

}
