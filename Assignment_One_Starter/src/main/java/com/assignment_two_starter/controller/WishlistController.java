package com.assignment_two_starter.controller;

import com.assignment_two_starter.dto.RemoveWishlistItemRequestDTO;
import com.assignment_two_starter.dto.WishlistRequestDTO;
import com.assignment_two_starter.dto.WishlistResponseDTO;
import com.assignment_two_starter.service.WishlistService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wishlist")
@SecurityRequirement(name = "BearerAuth")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    private boolean isCustomer(Authentication authentication) {
        return authentication != null &&
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(role -> role.equals("ROLE_CUSTOMER"));
    }

    private String getAuthenticatedEmail(UserDetails userDetails) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated.");
        }
        return userDetails.getUsername();
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createWishlist(
            Authentication authentication,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody WishlistRequestDTO requestDTO) {

        if (!isCustomer(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only customers can create a wishlist."));
        }

        if (requestDTO.getWishlistName() == null || requestDTO.getWishlistName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Wishlist name cannot be empty."));
        }

        WishlistResponseDTO wishlist = wishlistService.createWishlist(
                getAuthenticatedEmail(userDetails), requestDTO);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Wishlist created successfully.",
                "data", wishlist
        ));
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToWishlist(
            Authentication authentication,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody WishlistRequestDTO requestDTO) {

        if (!isCustomer(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only customers can add items to a wishlist."));
        }

        if (requestDTO.getWishlistName() == null || requestDTO.getProductId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Wishlist name and product ID are required."));
        }

        WishlistResponseDTO wishlist = wishlistService.addToWishlist(
                getAuthenticatedEmail(userDetails), requestDTO);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Product added to wishlist successfully.",
                "data", wishlist
        ));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getWishlists(
            Authentication authentication,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (!isCustomer(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only customers can view wishlists."));
        }

        List<WishlistResponseDTO> wishlists = wishlistService.getWishlists(getAuthenticatedEmail(userDetails));

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", wishlists.isEmpty() ? "No wishlists found." : "Wishlists retrieved successfully.",
                "data", wishlists
        ));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeItemFromWishlist(
            Authentication authentication,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody RemoveWishlistItemRequestDTO requestDTO) {

        if (!isCustomer(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only customers can remove wishlist items."));
        }

        if (requestDTO.getWishlistName() == null || requestDTO.getProductId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Wishlist name and product ID are required."));
        }

        WishlistResponseDTO wishlist = wishlistService.removeItemFromWishlist(
                getAuthenticatedEmail(userDetails), requestDTO);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Item removed from wishlist successfully.",
                "data", wishlist
        ));
    }

    @DeleteMapping("/deleteWishlist")
    public ResponseEntity<Map<String, Object>> deleteWishlist(
            Authentication authentication,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody WishlistRequestDTO requestDTO) {

        if (!isCustomer(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only customers can delete wishlists."));
        }

        if (requestDTO.getWishlistName() == null || requestDTO.getWishlistName().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Wishlist name is required."));
        }

        wishlistService.deleteWishlist(getAuthenticatedEmail(userDetails), requestDTO.getWishlistName());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Wishlist deleted successfully"
        ));
    }


    @Validated
    @GetMapping("/share")
    public ResponseEntity<Map<String, Object>> getShareableLink(
            Authentication authentication,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid WishlistRequestDTO requestDTO) {

        if (!isCustomer(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "status", "error",
                            "message", "Only customers can share wishlists.",
                            "timestamp", LocalDateTime.now()
                    ));
        }

        try {
            String email = getAuthenticatedEmail(userDetails);

            if (requestDTO.getWishlistName() == null || requestDTO.getWishlistName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "status", "error",
                                "message", "Wishlist name is required.",
                                "timestamp", LocalDateTime.now()
                        ));
            }

            String link = wishlistService.generateShareableLink(email, requestDTO.getWishlistName());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Shareable link generated successfully",
                    "link", link
            ));

        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                    "status", "error",
                    "message", ex.getReason(),
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "An unexpected error occurred.",
                            "timestamp", LocalDateTime.now()
                    ));
        }
    }

}
