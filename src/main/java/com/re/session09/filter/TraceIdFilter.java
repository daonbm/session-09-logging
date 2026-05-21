package com.re.session09.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j // Thêm annotation này để dùng log
public class TraceIdFilter extends OncePerRequestFilter {
    private static final String TRACE_ID = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 1. Tạo mã định danh duy nhất
            String traceId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // 2. QUAN TRỌNG: Đưa vào MDC để các hàm log sau này (Controller, Service) có thể truy xuất
            MDC.put(TRACE_ID, traceId);

            // 3. Trả về header cho Client
            response.addHeader("X-Trace-Id", traceId);

            // 4. TRACE: Ghi lại thông tin request cơ bản (URL, Method)
            // Dùng TRACE để tránh làm loãng log ở môi trường Production
            log.trace("Incoming Request: {} {}", request.getMethod(), request.getRequestURI());

            filterChain.doFilter(request, response);
        } finally {
            // 5. TRACE: Đánh dấu kết thúc xử lý request
            log.trace("Finished processing request.");

            // 6. Xóa dữ liệu MDC để tránh rò rỉ sang Thread khác (Thread Reuse)
            MDC.clear();
        }
    }
}