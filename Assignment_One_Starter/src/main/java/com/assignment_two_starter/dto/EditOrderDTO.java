package com.assignment_two_starter.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EditOrderDTO {
    private String shippingAddress;
    private List<OrderItemDTO> items;
    private String paymentMethod;
}
