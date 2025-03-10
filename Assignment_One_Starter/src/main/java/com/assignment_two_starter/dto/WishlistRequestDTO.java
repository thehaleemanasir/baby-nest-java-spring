package com.assignment_two_starter.dto;

import lombok.Data;

@Data
public class WishlistRequestDTO {
    private String wishlistName;
    private Integer productId;
    private String note;
}
