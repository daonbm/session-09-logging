package com.re.session09.service;

import java.util.Map;

public interface OrderService {
    Map<String, Object> processOrder(String userId, double totalAmount) throws Exception;
    Map<String, Object> getOrderById(String orderId);
}
