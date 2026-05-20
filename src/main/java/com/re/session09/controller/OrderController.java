package com.re.session09.controller;

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
        // Controller layer has more code than the service layer
        log.info("Received request to create a new order");

        // Validate basic payload info without logging sensitive user details
        if (!orderRequest.containsKey("userId") || !orderRequest.containsKey("totalAmount")) {
            // Use WARN for validation or bad request issues that don't crash the app
            log.warn("Order creation failed: Missing mandatory fields 'userId' or 'quantity'. " +
                     "Payload keys received: {}", orderRequest.keySet());

            return ResponseEntity.badRequest().body(Map.of("error", "Invalid order payload"));
        }

        String userId = orderRequest.get("userId").toString();
        int totalAmount = (int) orderRequest.get("totalAmount");

        // 4. Best Practice: Use parameterized placeholders '{}' instead of string concatenation (+)
        // This is highly efficient because strings are only built if DEBUG level is active.
        log.debug("Processing placement context -> User ID: {}, Total Amount: {}", userId, totalAmount);

        try {
            // Gọi Service (Nơi chứa cả logic thành công và 2 tầng logic thất bại)
            Map<String, Object> result = orderService.createOrder(userId, totalAmount);

            log.info("The order has been successfully created for customer [{}]", userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            // 5. Best Practice: Use ERROR level for runtime exceptions.
            // Passing the exception object 'e' as the last parameter tells SLF4J to log the entire stack trace.
            log.error("Critical failure encountered during the order finalization pipeline.", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An internal error occurred while processing your order"));
        }
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<?> getOrderById(@PathVariable String id) {
//        log.info("Fetching details for Order ID: {}", id);
//
//        // Simulating an order lookup
//        if (!orderStorage.containsKey(id)) {
//            log.warn("Query failed: Order code [{}] does not exist in the system: ", id);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("message", "No order found."));
//        }
//
//        Map<String, Object> order = orderStorage.get(id);
//        log.debug("Results of retrieving records from temporary memory: {}", order);
//
//        return ResponseEntity.ok(order);
//    }
}
