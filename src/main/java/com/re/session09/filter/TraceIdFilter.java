package com.re.session09.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TraceIdFilter extends OncePerRequestFilter {
    private static final String TRACE_ID = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Create random trace id for each request
            String traceId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // Return traceId to header for client also know
            response.addHeader("X-Trace-Id", traceId);

            filterChain.doFilter(request, response);
        } finally {
            // Important: Must clear MDC after finishing request to prevent leak data between
            MDC.clear();
        }
    }
}
