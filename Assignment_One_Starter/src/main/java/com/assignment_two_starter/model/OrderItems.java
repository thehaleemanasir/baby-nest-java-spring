package com.assignment_two_starter.model;

import java.io.Serializable;
import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @author Alan.Ryan
 */
@Entity
@Table(name = "order_items")
@Data
public class OrderItems implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "order_item_id")
    private Integer orderItemId;

    @Basic(optional = false)
    @Column(name = "quantity")
    private int quantity;


    // @Max(value=?)  @Min(value=?)//if you know the range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "unit_price")

    private BigDecimal unitPrice;

    @Basic(optional = false)
    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    @ManyToOne(optional = false)
    @ToString.Exclude
    private Orders order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    public void setPrice(BigDecimal price) {
        this.unitPrice = price;
    }

    public boolean isShipped() {
        return order.getStatus().equalsIgnoreCase("shipped");
    }

    public boolean isPaid() {
        return order.getPaymentStatus().equalsIgnoreCase("paid");
    }

    public String getPaymentStatus() {
        return order.getPaymentStatus();
    }

    public String getShippingStatus() {
        return order.getStatus();
    }
}
