package com.assignment_two_starter.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@JacksonXmlRootElement(localName = "OrderItem")
@XmlRootElement(name = "OrderItem")
public class OrderItemDTO {
    private Integer productId;
    private String productName;
    private Double unitPrice;
    private Integer quantity;
    private Double totalPrice;

    public OrderItemDTO(Integer productId, String productName, Double unitPrice, Integer quantity, Double totalPrice) {
       this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public OrderItemDTO(Integer productId, String name, BigDecimal bigDecimal, int quantity, BigDecimal bigDecimal1) {
        this.productId = productId;
        this.productName = name;
        this.unitPrice = bigDecimal.doubleValue();
        this.quantity = quantity;
        this.totalPrice = bigDecimal1.doubleValue();
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
