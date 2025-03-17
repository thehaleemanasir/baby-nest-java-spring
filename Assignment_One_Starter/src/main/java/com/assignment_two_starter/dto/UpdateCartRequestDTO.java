package com.assignment_two_starter.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateCartRequestDTO {
    @NotNull(message = "Product ID is required")
    private Integer productId;

    @NotNull(message = "New quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer newQuantity;

}
