package com.core.market.common.security.handler;

import com.core.market.common.ErrorCode;
import com.core.market.common.Response;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class NoRedirectAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(ErrorCode.UNAUTHORIZED_USER.getHttpStatus().value());
        response.getWriter().write(Response.error(ErrorCode.UNAUTHORIZED_USER.name()).toStream());
    }
}
