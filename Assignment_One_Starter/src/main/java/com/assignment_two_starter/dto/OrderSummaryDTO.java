package com.assignment_two_starter.dto;

import com.assignment_two_starter.model.Orders;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JacksonXmlRootElement(localName = "OrderSummary")
@XmlRootElement(name = "OrderSummary")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderSummaryDTO {
    private Integer orderId;
    private String customerName;
    private String shippingAddress;
    private String status;
    private Double totalAmount;
    private String paymentStatus;

    @XmlElementWrapper(name = "orderItems")
    @XmlElement(name = "OrderItem")
    private List<OrderItemDTO> orderItems;

    public OrderSummaryDTO() {}

    public OrderSummaryDTO(Integer orderId, String customerName, String shippingAddress,
                           String status, Double totalAmount, String paymentStatus,
                           List<OrderItemDTO> orderItems) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.shippingAddress = shippingAddress;
        this.status = status;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.orderItems = orderItems;
    }

    public OrderSummaryDTO(Orders orders) {
        this.orderId = orders.getOrderId();
        this.customerName = orders.getCustomer().getFirstName() + " " + orders.getCustomer().getLastName();
        this.shippingAddress = (orders.getShippingAddressId() != null) ?
                orders.getShippingAddressId().getStreetAddress() : "No Address Provided";
        this.status = orders.getStatus().toString();
        this.totalAmount = orders.getTotalAmount().doubleValue();
        this.paymentStatus = (!orders.getPaymentsList().isEmpty()) ?
                orders.getPaymentsList().get(0).getStatus() : "No Payment Found";
    }


    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }




    public boolean containsKey(Integer id) {
        return false;
    }
}
