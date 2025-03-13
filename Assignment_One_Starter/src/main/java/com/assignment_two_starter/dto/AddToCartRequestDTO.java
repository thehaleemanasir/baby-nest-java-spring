package com.assignment_two_starter.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequestDTO {

    @NotNull(message = "Product ID is required")
    private Integer productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
