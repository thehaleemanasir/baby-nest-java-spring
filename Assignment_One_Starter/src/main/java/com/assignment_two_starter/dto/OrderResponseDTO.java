package com.assignment_two_starter.dto;
import com.assignment_two_starter.model.Orders;
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
    private List<OrderItemCustomerDTO> items;
    private BigDecimal totalAmount;
    private String status;
    private String paymentStatus;

    public OrderResponseDTO(Integer orderId, String customerEmail, String shippingAddress,
                            List<OrderItemCustomerDTO> items, BigDecimal totalAmount,
                            String status, String paymentStatus) {
        this.orderId = orderId;
        this.customerEmail = customerEmail;
        this.shippingAddress = shippingAddress;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    public OrderResponseDTO(Orders order) {
        this.orderId = order.getOrderId();
        this.customerEmail = order.getCustomer().getEmail();
        this.shippingAddress = order.getShippingAddress();
        this.totalAmount = order.getTotalAmount();
        this.status = order.getStatus();
        this.paymentStatus = order.getPaymentStatus();
    }
}
