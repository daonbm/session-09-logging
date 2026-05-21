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
        log.info("REST request to create Order for userId: {}", orderRequest.get("userId"));
        log.trace("Order request payload: {}", orderRequest);

        if (!orderRequest.containsKey("userId") || !orderRequest.containsKey("totalAmount")) {
            log.warn("Validation failed: Missing mandatory fields. Received: {}", orderRequest.keySet());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid order payload"));
        }

        try {
            String userId = orderRequest.get("userId").toString();
            int totalAmount = (int) orderRequest.get("totalAmount");

            log.debug("Parsed order data -> User: {}, Amount: {}", userId, totalAmount);

            Map<String, Object> result = orderService.processOrder(userId, totalAmount);

            log.info("Order successfully created for customer [{}]", userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (ExceedingLimitException e) {

            log.warn("Order rejected: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {

            log.error("Unexpected failure during order creation.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An internal error occurred"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable String id) {
        log.trace("Entering getOrderById endpoint with path variable id: {}", id);
        log.info("Fetching details for Order ID: [{}]", id);

        Map<String, Object> order = orderService.getOrderById(id);

        if (order == null) {
            log.warn("Lookup failed: Order code [{}] does not exist.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", String.format("Order not found with ID: %s", id)));
        }

        log.debug("Successfully retrieved order data: {}", order);

        return ResponseEntity.ok(order);
    }
}
