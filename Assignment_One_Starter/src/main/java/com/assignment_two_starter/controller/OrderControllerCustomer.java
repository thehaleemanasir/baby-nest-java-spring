package com.assignment_two_starter.controller;

import com.assignment_two_starter.dto.OrderRequestDTO;
import com.assignment_two_starter.dto.OrderResponseDTO;
import com.assignment_two_starter.service.OrderCustomerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
@SecurityRequirement(name = "BearerAuth")
public class OrderControllerCustomer {

    private final OrderCustomerService orderServiceCustomer;

    public OrderControllerCustomer(OrderCustomerService orderServiceCustomer) {
        this.orderServiceCustomer = orderServiceCustomer;
    }

    @PostMapping("/place")
    public ResponseEntity<Object> placeOrder(
            Authentication authentication,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OrderRequestDTO requestDTO) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse("Unauthorized access"));
        }

        if (requestDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errorResponse("Order request body is missing."));
        }

        if (requestDTO.getItems() == null || requestDTO.getItems().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errorResponse("Shopping cart is empty. Add items before placing an order."));
        }

        try {
            OrderResponseDTO response = orderServiceCustomer.placeOrder(userDetails.getUsername(), requestDTO);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(errorResponse("Order processing failed."));
            }

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("status", "success");
            responseMap.put("message", "Order placed successfully");
            responseMap.put("order", response);

            return ResponseEntity.ok(responseMap);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("An error occurred while processing the order: " + ex.getMessage()));
        }
    }

    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("status", "error");
        errorMap.put("message", message);
        errorMap.put("timestamp", LocalDateTime.now());
        return errorMap;
    }

    @PutMapping("/edit/{orderId}")
    public ResponseEntity<?> editOrder(@PathVariable Long orderId,
                                       @RequestBody OrderRequestDTO orderRequest,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        try {
            OrderResponseDTO updatedOrder = orderServiceCustomer.editOrder(orderId, orderRequest, email);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Something went wrong"));
        }
    }
}
