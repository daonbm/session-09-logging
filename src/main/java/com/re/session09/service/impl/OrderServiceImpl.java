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
    private final Map<String, Map<String, Object>> orderStorage = new HashMap<>();

    @Override
    public Map<String, Object> processOrder(String userId, double totalAmount) {
        MDC.put("user", userId);

        log.trace("Method [processOrder] started. Params: userId={}, amount={}", userId, totalAmount);

        if (totalAmount > 100000) {
            log.error("BUSINESS_RULE_VIOLATION: User [{}] attempted to pay {} which exceeds limit!", userId, totalAmount);
            throw new ExceedingLimitException("The transaction limit has been exceeded!");
        }

        log.debug("Preparing to persist new order to storage for user: {}", userId);

        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("orderId", orderId);
        orderDetails.put("userId", userId);
        orderDetails.put("totalAmount", totalAmount);
        orderDetails.put("status", "CREATED");

        orderStorage.put(orderId, orderDetails);

        log.info("The order [{}] has been successfully created for customer [{}]", orderId, userId);
        return orderDetails;
    }

    @Override
    public Map<String, Object> getOrderById(String orderId) {
        log.debug("Fetching order details for ID: {}", orderId);
        Map<String, Object> order = orderStorage.get(orderId);

        if (order == null) {
            log.warn("Order with ID [{}] not found in storage.", orderId);
            return null;
        }

        log.info("Successfully retrieved order [{}]", orderId);
        return order;
    }
}
