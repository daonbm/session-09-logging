package com.re.session09.service.impl;

import com.re.session09.exception.ExceedingLimitException;
import com.re.session09.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    // Giả lập Database trong Service
    private final Map<String, Map<String, Object>> orderStorage = new HashMap<>();

    @Override
    public Map<String, Object> processOrder(String userId, double totalAmount) throws Exception {
        // Put userId to MDC to trace all of order's processing of this user
        MDC.put("user", userId);

        // TRACE: Đánh dấu điểm bắt đầu luồng xử lý sâu nhất
        log.trace("Method [processOrder] started. Params: userId={}, amount={}", userId, totalAmount);

        if (totalAmount > 100000) {
            // ERROR: Một tình huống nghiêm trọng (Ví dụ: Fraud detection hoặc lỗi thanh toán)
            log.error("BUSINESS_RULE_VIOLATION: User [{}] attempted to pay {} which exceeds limit!", userId, totalAmount);
            throw new ExceedingLimitException("The transaction limit has been exceeded!");
        }

        // DEBUG: Log trạng thái trước khi thay đổi Database giả lập
        log.debug("Preparing to persist new order to storage for user: {}", userId);

        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("orderId", orderId);
        orderDetails.put("userId", userId);
        orderDetails.put("totalAmount", totalAmount);
        orderDetails.put("status", "CREATED");

        orderStorage.put(orderId, orderDetails); // save to mock database

        // INFO: Sự kiện quan trọng của nghiệp vụ
        log.info("The order [{}] has been successfully created for customer [{}]", orderId, userId);

        return orderDetails;
    }

    @Override
    public Map<String, Object> getOrderById(String orderId) {
        // DEBUG: Log để kiểm tra tham số đầu vào khi cần fix bug
        log.debug("Fetching order details for ID: {}", orderId);

        // Truy xuất từ "giả lập Database" orderStorage
        Map<String, Object> order = orderStorage.get(orderId);

        if (order == null) {
            // WARN: Log cảnh báo nếu ID không tồn tại
            log.warn("Order with ID [{}] not found in storage.", orderId);

            // Bạn có thể ném một Exception hoặc trả về một Map chứa thông báo lỗi
            // Ở đây tôi trả về null hoặc một Map trống có tính chất thông báo
            return null;
        }

        // INFO: Chỉ log khi tìm thấy đơn hàng thành công
        log.info("Successfully retrieved order [{}]", orderId);
        return order;
    }
}
