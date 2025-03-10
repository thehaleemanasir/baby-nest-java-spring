package com.assignment_two_starter.dto;

import com.assignment_two_starter.model.WishlistProduct;

public class ProductDTO {
    private Long id;
    private String name;
    private String note;

    public ProductDTO(WishlistProduct wishlistProduct) {
        this.id = Long.valueOf(wishlistProduct.getProduct().getId());
        this.name = wishlistProduct.getProduct().getName();
        this.note = wishlistProduct.getNote();
    }

    // Getters and Setters
}
