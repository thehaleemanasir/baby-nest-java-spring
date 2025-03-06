package com.assignment_two_starter.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequestDTO {
    private String shippingAddress;
    private String paymentMethod;
}
