package com.assignment_two_starter.dto;

import com.assignment_two_starter.model.Wishlist;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WishlistResponseDTO {
    @Getter
    private Integer id;
    @Getter
    private String wishlistName;
    private LocalDateTime createdAt;
    // Getters and Setters
    @Getter
    private List<Map<String, Object>> products;

    public WishlistResponseDTO(Wishlist wishlist) {
        this.id = wishlist.getId();
        this.wishlistName = wishlist.getWishlistName();
        this.createdAt = LocalDateTime.parse(wishlist.getCreatedAt().toString());
        this.products = wishlist.getWishlistProducts().stream()
                .map(wp -> Map.<String, Object>of(
                        "id", wp.getProduct().getId(),
                        "name", wp.getProduct().getName(),
                        "note", wp.getNote() != null ? wp.getNote() : ""
                ))
                .collect(Collectors.toList());
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;


    }
}

