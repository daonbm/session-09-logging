package com.re.session09.service.impl;

import com.re.session09.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Override
    public void processOrder(String userId, double amount) {
        // TRACE: Extremely detailed tracking
        log.trace("Entering processOrder method for user: {}", userId);

        // DEBUG: Diagnostic info for Dev environment
        log.debug("Processing order details - User: {}, Amount: {}", userId, amount);

        if (amount <= 0) {
            // WARN: Abnormal but system continues
            // Note: Use WARN for user input errors, not ERROR
            log.warn("Potential malicious activity: User {} attempted a zero/negative order.", userId);
            return;
        }

        try {
            boolean success = saveToDatabase();

            if (success) {
                // INFO: Business events (Default for Production)
                log.info("Order successfully placed for User ID: {}", userId);
            }
        } catch (Exception e) {
            // ERROR: Actual system failure
            log.error("Critical failure during order processing for User {}: ", userId, e);
        }
    }

    private boolean saveToDatabase() throws Exception {
        throw new Exception("Database Connection Lost");
    }
}
