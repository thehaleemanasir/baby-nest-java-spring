package com.assignment_two_starter.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Data
public class OrderRequestDTO {
    private String shippingAddress;
    private String city;
    private String county;
    private String postalCode;
    private String country;
    private String paymentMethod;
    @Getter
    private List<CartItemDTO> items;

}
