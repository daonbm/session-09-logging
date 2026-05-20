package com.re.session09.service.impl;

import com.re.session09.service.OrderService;
import lombok.extern.slf4j.Slf4j;
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
    public Map<String, Object> createOrder(String userId, double totalAmount) throws Exception {
        // --- GIẢ LẬP TRƯỜNG HỢP THẤT BẠI ---
        // Nếu số tiền > 100,000, giả sử hệ thống thanh toán lỗi hoặc vượt hạn mức
        if (totalAmount > 100000) {
            log.error("Order creation failed for User [{}]: Amount {} exceeds limit.", userId, totalAmount);
            throw new Exception("Payment Gateway Timeout hoặc Vượt hạn mức giao dịch!");
        }

        // 2. Gọi processOrder để kiểm tra lỗi Hệ thống (Database connection)
        // Method này sẽ throw RuntimeException vì saveToDatabase() luôn lỗi
        this.processOrder(userId, totalAmount); // để test trường hợp đặt đợt thất bại

        // 3. Nếu vượt qua được 2 bước trên (giả sử sau này bạn sửa processOrder hết lỗi)
        // thì mới thực hiện tạo đơn hàng thành công
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("orderId", orderId);
        orderDetails.put("userId", userId);
        orderDetails.put("totalAmount", totalAmount);
        orderDetails.put("status", "CREATED");
        orderStorage.put(orderId, orderDetails); // save to mock database

        log.info("The order [{}] has been successfully created for customer [{}]", orderId, userId);
        return orderDetails;
    }

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
            saveToDatabase();
        } catch (Exception e) {
            // ERROR: Actual system failure
            log.error("Critical failure during order processing for User {}: ", userId, e);
//            throw new RuntimeException(e);
        }
    }

    private void saveToDatabase() throws Exception {
        throw new Exception("Database Connection Lost");
    }
}
