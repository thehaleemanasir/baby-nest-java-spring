package com.assignment_two_starter.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemCustomerDTO {
    private Long productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    public OrderItemCustomerDTO(com.assignment_two_starter.model.OrderItems orderItem) {
        this.productId = Long.valueOf(orderItem.getProduct().getProductId());
        this.quantity = orderItem.getQuantity();
        this.unitPrice = orderItem.getUnitPrice();
        this.totalPrice = orderItem.getTotalPrice();
    }


}
