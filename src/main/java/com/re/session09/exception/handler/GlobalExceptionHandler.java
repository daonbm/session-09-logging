package com.re.session09.exception.handler;

import com.re.session09.exception.ExceedingLimitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(ExceedingLimitException.class)
    public ResponseEntity<Map<String, String>> handleProductNotFound(ExceedingLimitException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "EXCEEDING_TRANSACTION_LIMIT");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex) {
        return new ResponseEntity<>("System error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
