package com.assignment_two_starter.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "wishlist_product")
public class WishlistProduct {

    @EmbeddedId
    private WishlistProductId id;

    @Getter
    @Setter
    @ManyToOne
    @MapsId("wishlistId")
    @JoinColumn(name = "wishlist_id")
    private Wishlist wishlist;

    @Getter
    @Setter
    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @Getter
    @Setter
    private String note;

    public WishlistProduct() {
        this.id = new WishlistProductId();
        this.wishlist = new Wishlist();
        this.product = new Product();
        this.note = "";
    }


    // Getters and Setters
}
