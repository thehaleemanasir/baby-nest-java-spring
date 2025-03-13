package com.assignment_two_starter.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateCartRequestDTO {
    @NotNull(message = "Product ID is required")
    private Integer productId;

    @NotNull(message = "New quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer newQuantity;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(Integer newQuantity) {
        this.newQuantity = newQuantity;
    }
}
