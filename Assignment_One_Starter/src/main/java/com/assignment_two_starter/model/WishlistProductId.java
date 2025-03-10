package com.assignment_two_starter.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
@Embeddable
public class WishlistProductId implements Serializable {

    private Integer wishlistId;
    private Integer productId;

}
