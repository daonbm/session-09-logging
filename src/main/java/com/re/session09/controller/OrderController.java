package com.re.session09.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    // Simulate temporary memory to storage order list
    private final Map<String, Map<String, Object>> orderStorage = new HashMap<>();

    @GetMapping
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
            // Simulate processing database & logic operations
            String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            log.info("Successfully created order with ID: {}", orderId);

            Map<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("orderId", orderId);
            orderDetails.put("status", "CREATED");

            // Save to mock database
            orderStorage.put(orderId, orderDetails);

            log.info("The order [{}] has been successfully created for customer [{}]", orderId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(orderDetails);
        } catch (Exception e) {
            // 5. Best Practice: Use ERROR level for runtime exceptions.
            // Passing the exception object 'e' as the last parameter tells SLF4J to log the entire stack trace.
            log.error("Critical failure encountered during the order finalization pipeline.", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An internal error occurred while processing your order"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable String id) {
        log.info("Fetching details for Order ID: {}", id);

        // Simulating an order lookup
        if ("ORD-NOTFOUND".equals(id)) {
            log.warn("Lookup execution targeted a missing entity record: {}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Map.of(
                "orderId", id,
                "amount", 150.00,
                "currency", "USD"
        ));
    }
}
