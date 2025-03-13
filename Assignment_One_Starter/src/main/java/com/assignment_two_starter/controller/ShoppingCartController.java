package com.assignment_two_starter.controller;

import com.assignment_two_starter.dto.AddToCartRequestDTO;
import com.assignment_two_starter.dto.CartResponseDTO;
import com.assignment_two_starter.dto.UpdateCartRequestDTO;
import com.assignment_two_starter.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    private String getAuthenticatedEmail(UserDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("User not authenticated.");
        }
        return userDetails.getUsername();
    }

    @GetMapping
    public ResponseEntity<Object> getCart(Authentication authentication, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse("Unauthorized access.", new RuntimeException("Unauthorized access.")));
            }
            String email = getAuthenticatedEmail(userDetails);
            CartResponseDTO cart = shoppingCartService.getActiveCartForCustomer(email);
            return ResponseEntity.ok(cart);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse("Failed to retrieve cart", ex));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addToCart(
            Authentication authentication,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AddToCartRequestDTO request) {

        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Unauthorized access"));
        }

        try {
            CartResponseDTO response = shoppingCartService.addToCart(userDetails.getUsername(), request);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Item added to cart successfully",
                    "cart", response
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse("Error adding item to cart", ex));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateCartItem(
            Authentication authentication,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateCartRequestDTO requestDTO) {

        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Unauthorized access"));
        }

        if (requestDTO.getProductId() == null || requestDTO.getNewQuantity() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Product ID and quantity are required"));
        }

        try {
            CartResponseDTO updatedCart = shoppingCartService.updateCartItem(userDetails.getUsername(), requestDTO);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Cart item updated successfully",
                    "cart", updatedCart
            ));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                    "status", "error",
                    "message", ex.getReason(),
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "An unexpected error occurred."));
        }
    }


    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Object> removeFromCart(
            Authentication authentication,
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer productId) {

        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Unauthorized access"));
        }

        try {
            CartResponseDTO response = shoppingCartService.removeCartItem(userDetails.getUsername(), productId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Item removed from cart successfully",
                    "cart", response
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse("Error removing item from cart", ex));
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Object> clearCart(
            Authentication authentication,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Unauthorized access"));
        }

        try {
            shoppingCartService.clearCart(userDetails.getUsername());
            return ResponseEntity.ok(Map.of("status", "success", "message", "Cart cleared successfully"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse("Error clearing cart", ex));
        }
    }

    private Map<String, Object> errorResponse(String message, Exception ex) {
        return Map.of(
                "status", "error",
                "message", message,
                "errorDetails", ex.getMessage(),
                "timestamp", LocalDateTime.now()
        );
    }
}
