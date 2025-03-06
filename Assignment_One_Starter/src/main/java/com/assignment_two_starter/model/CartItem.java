package com.assignment_two_starter.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "cart_items")
@Data
public class CartItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "cart_item_id")
    private Integer cartItemId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "added_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date addedAt;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CartItemStatus status = CartItemStatus.ACTIVE;

    public enum CartItemStatus {
        ACTIVE, REMOVED
    }

    @PrePersist
    protected void onAdd() {
        this.addedAt = new Date();
    }
}
