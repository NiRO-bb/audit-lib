package com.example.audit_lib_spring_boot_starter.interceptors.wrappers;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * Represents a wrapper for http-response.
 * Gives opportunity to read response body many times.
 */
public class IncomingResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintWriter printWriter = new PrintWriter(outputStream);

    public IncomingResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {}

            @Override
            public void write(int b) {
                outputStream.write(b);
            }
        };
    }

    @Override
    public PrintWriter getWriter() {
        return printWriter;
    }

    public byte[] getResponseAsByte() {
        printWriter.flush();
        return outputStream.toByteArray();
    }
}
