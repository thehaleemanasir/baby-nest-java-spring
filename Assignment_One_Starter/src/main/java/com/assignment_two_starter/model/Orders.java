package com.assignment_two_starter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Alan.Ryan
 */
@Entity
@Table(name = "orders")
@Data
public class Orders implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "order_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Basic(optional = false)
     @Column(name = "status", nullable = false)
    private String status;


    @Basic(optional = false)
    @Column(name = "address_change_fee")
    private Double addressChangeFee;

    @Column(name = "estimated_Shipping_Date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date estimatedShippingDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    @ToString.Exclude
    private List<Payment> paymentsList;

    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    @ToString.Exclude
    private Customer customer;

    @JoinColumn(name = "shipping_address_id", referencedColumnName = "address_id")
    @ManyToOne
    @ToString.Exclude
    private Address shippingAddressId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    @ToString.Exclude
    private List<OrderItems> orderItemsList;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItems> orderItems;

    public void setCustomerId(Integer userId) {
        this.customer.setUserId(userId);
    }

    public void setEstimatedDeliveryDate(LocalDate localDate) {
        this.estimatedShippingDate = java.sql.Date.valueOf(localDate);
    }

    @PrePersist
    protected void onCreate() {
        this.orderDate = new Date();
    }

    public String getPaymentStatus() {
        return paymentsList.isEmpty() ? "Pending" : paymentsList.get(0).getStatus();
    }

    public void setShippingAddress(Address address) {
        this.shippingAddressId = address;
    }


    public void setCity(String city) {
        this.shippingAddressId.setCity(city);
    }

    public void setCounty(String county) {
        this.shippingAddressId.setCounty(county);
    }

    public void setPostalCode(String postalCode) {
        this.shippingAddressId.setPostalCode(postalCode);
    }

    public void setCountry(String country) {
        this.shippingAddressId.setCountry(country);
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentsList.get(0).setPaymentMethod(paymentMethod);
    }

    public String getShippingAddress() {
        return this.shippingAddressId.getStreetAddress();
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddressId.setStreetAddress(shippingAddress);
    }

//    public enum OrderStatus {
//        PENDING,
//        SHIPPED,
//        DELIVERED,
//        PROCESSING,
//        COMPLETED,
//        CANCELLED
//    }

}