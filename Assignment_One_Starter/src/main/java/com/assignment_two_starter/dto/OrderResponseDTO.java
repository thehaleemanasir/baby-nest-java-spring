package com.assignment_two_starter.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponseDTO {
    private Integer orderId;
    private String customerEmail;
    private String shippingAddress;
    private List<OrderItemDTO> items;
    private BigDecimal totalAmount;
    private String status;
    private String paymentStatus;

    public OrderResponseDTO(Integer orderId, String customerEmail, String shippingAddress,
                            List<OrderItemDTO> items, BigDecimal totalAmount,
                            String status, String paymentStatus) {
        this.orderId = orderId;
        this.customerEmail = customerEmail;
        this.shippingAddress = shippingAddress;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }
}
