package com.example.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;

public final class ErrorJsonWriter {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ErrorJsonWriter() {}

    public static void write(HttpServletResponse response, int status, String error,
                             String message, String path) throws IOException {
        if (response.isCommitted()) return;

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        Map<String, Object> body = Map.of(
                "status", status,
                "error", error,
                "message", message,
                "path", path,
                "timestamp", OffsetDateTime.now().toString()
        );

        MAPPER.writeValue(response.getWriter(), body);
        response.getWriter().flush();
    }
}
