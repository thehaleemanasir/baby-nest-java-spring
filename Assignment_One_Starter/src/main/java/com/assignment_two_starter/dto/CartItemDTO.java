package com.assignment_two_starter.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartItemDTO {
    private Integer productId;
    private String productName;
    private double price;
    private int quantity;
    private double totalPrice;

    public CartItemDTO(Integer productId, String productName, double price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = price * quantity;
    }

}
