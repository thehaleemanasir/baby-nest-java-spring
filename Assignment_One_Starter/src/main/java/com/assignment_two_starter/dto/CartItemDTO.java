package com.assignment_two_starter.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDTO {

    private Integer productId;
    private String productName;
    private int quantity;
    private double price;
    private double totalPrice;

    public CartItemDTO(Integer productId, String productName, int quantity, double price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = Double.parseDouble(String.format("%.2f", price));
        this.totalPrice = Double.parseDouble(String.format("%.2f", quantity * price));
    }


}
