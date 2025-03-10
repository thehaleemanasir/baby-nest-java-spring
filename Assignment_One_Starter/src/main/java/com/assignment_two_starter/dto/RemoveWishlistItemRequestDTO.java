package com.assignment_two_starter.dto;

import lombok.Data;

@Data
public class RemoveWishlistItemRequestDTO {
    private String wishlistName;
    private Integer productId;
}
