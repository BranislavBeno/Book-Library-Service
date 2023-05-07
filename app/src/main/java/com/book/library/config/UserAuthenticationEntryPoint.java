package com.book.library.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

public class UserAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public UserAuthenticationEntryPoint() {
        objectMapper = new ObjectMapper();
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
            throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName());
        response.getOutputStream().println(objectMapper.writeValueAsString(CommonProblem.unauthorized()));
    }
}
