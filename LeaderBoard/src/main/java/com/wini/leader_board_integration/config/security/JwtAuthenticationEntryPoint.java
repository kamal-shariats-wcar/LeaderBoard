package com.wini.leader_board_integration.config.security;


import com.wini.leader_board_integration.data.JWTExceptionType;
import com.wini.leader_board_integration.exception.JWTCustomException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -8970718410437077606L;
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        Exception ex = (Exception) request.getAttribute("exception");
        JWTExceptionType type = null;

        if (ex != null) {
            if (ex instanceof ExpiredJwtException)
                type = JWTExceptionType.EXPIRED;
            if (ex instanceof UnsupportedJwtException)
                type = JWTExceptionType.UNSUPPORTED;
            if (ex instanceof MalformedJwtException)
                type = JWTExceptionType.MALFORMED;
            if (ex instanceof SignatureException)
                type = JWTExceptionType.SIGNATURE;
            if (ex instanceof IllegalArgumentException)
                type = JWTExceptionType.ILLEGAL_ARGUMENT;
            resolver.resolveException(request, response, null, new JWTCustomException(type, ex.getMessage()));
        }
    }
}
