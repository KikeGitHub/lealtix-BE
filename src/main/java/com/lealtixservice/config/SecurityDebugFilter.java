package com.lealtixservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class SecurityDebugFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(SecurityDebugFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String path = request.getRequestURI();
            String method = request.getMethod();
            String origin = request.getHeader("Origin");
            String authHeader = request.getHeader("Authorization");

            log.debug("[SEC-DEBUG] Incoming request {} {} Origin: {} Authorization: {}", method, path, origin, authHeader != null ? "present" : "absent");

            // log headers briefly
            Enumeration<String> names = request.getHeaderNames();
            if (names != null) {
                StringBuilder sb = new StringBuilder();
                Collections.list(names).forEach(h -> sb.append(h).append(":").append(request.getHeader(h)).append("; "));
                log.trace("[SEC-DEBUG] Headers: {}", sb.toString());
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                log.debug("[SEC-DEBUG] Authentication: {}, authenticated={}, authorities={}", auth.getName(), auth.isAuthenticated(), auth.getAuthorities());
            } else {
                log.debug("[SEC-DEBUG] No authentication present (anonymous)");
            }
        } catch (Exception e) {
            log.warn("[SEC-DEBUG] Error while logging security debug info: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}

