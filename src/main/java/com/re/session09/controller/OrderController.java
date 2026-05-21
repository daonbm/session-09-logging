package com.re.session09.controller;

import com.re.session09.exception.ExceedingLimitException;
import com.re.session09.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> orderRequest) {
        // INFO: Xác nhận hệ thống đã nhận được tín hiệu từ Client
        log.info("REST request to create Order for userId: {}", orderRequest.get("userId"));

        // TRACE: Ghi lại toàn bộ Payload thô để kiểm tra định dạng nếu cần
        log.trace("Order request payload: {}", orderRequest);

        // Validate basic payload info without logging sensitive user details
        if (!orderRequest.containsKey("userId") || !orderRequest.containsKey("totalAmount")) {
            // WARN: Client gửi sai dữ liệu (Bad Request). Không phải lỗi hệ thống nhưng cần lưu ý.
            log.warn("Validation failed: Missing mandatory fields. Received: {}", orderRequest.keySet());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid order payload"));
        }

        try {
            String userId = orderRequest.get("userId").toString();
            int totalAmount = (int) orderRequest.get("totalAmount");

            // DEBUG: Log các giá trị đã được parse để chuẩn bị gọi Service
            log.debug("Parsed order data -> User: {}, Amount: {}", userId, totalAmount);

            Map<String, Object> result = orderService.processOrder(userId, totalAmount);

            // INFO: Chỉ log thành công ngắn gọn
            log.info("Order successfully created for customer [{}]", userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (ExceedingLimitException e) {
            // WARN: Đây là lỗi nghiệp vụ (Business Logic), không phải crash ứng dụng
            log.warn("Order rejected: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // ERROR: Lỗi không xác định, cần stack trace để điều tra
            log.error("Unexpected failure during order creation.", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An internal error occurred"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable String id) {
        // TRACE: Đánh dấu điểm bắt đầu thâm nhập vào hàm ở mức thấp nhất
        log.trace("Entering getOrderById endpoint with path variable id: {}", id);

        // INFO: Ghi nhận sự kiện nghiệp vụ quan trọng (Truy vấn đơn hàng)
        log.info("Fetching details for Order ID: [{}]", id);

        Map<String, Object> order = orderService.getOrderById(id);

        if (order == null) {
            // WARN: Client gửi ID không tồn tại.
            // Lưu ý: Không dùng "+" để nối chuỗi trong Map.of để code sạch hơn.
            log.warn("Lookup failed: Order code [{}] does not exist.", id);

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", String.format("Order not found with ID: %s", id)));
        }

        // DEBUG: Ghi lại dữ liệu chi tiết trả về để phục vụ kiểm tra logic/mapping
        log.debug("Successfully retrieved order data: {}", order);

        return ResponseEntity.ok(order);
    }
}
