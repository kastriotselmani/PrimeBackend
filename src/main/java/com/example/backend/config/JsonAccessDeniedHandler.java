package com.example.backend.config;

import jakarta.servlet.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        ErrorJsonWriter.write(
                response,
                HttpServletResponse.SC_FORBIDDEN,
                "Forbidden",
                "Access is denied",
                request.getRequestURI()
        );
    }
}
