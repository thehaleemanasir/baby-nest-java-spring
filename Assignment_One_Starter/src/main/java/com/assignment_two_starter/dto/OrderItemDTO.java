package com.assignment_two_starter.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@JacksonXmlRootElement(localName = "OrderItem")
@XmlRootElement(name = "OrderItem")
public class OrderItemDTO {
    private String productName;
    private Double unitPrice;
    private Integer quantity;
    private Double totalPrice;

    public OrderItemDTO(String productName, Double unitPrice, Integer quantity, Double totalPrice) {
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
