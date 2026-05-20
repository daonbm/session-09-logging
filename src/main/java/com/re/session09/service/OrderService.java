package com.re.session09.service;

import java.util.Map;

public interface OrderService {
    Map<String, Object> createOrder(String userId, double totalAmount) throws Exception;
    void processOrder(String userId, double amount);
}
