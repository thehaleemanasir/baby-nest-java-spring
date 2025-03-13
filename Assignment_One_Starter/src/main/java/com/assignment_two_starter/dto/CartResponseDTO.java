package com.assignment_two_starter.dto;

import com.assignment_two_starter.model.CartItem;
import com.assignment_two_starter.model.ShoppingCart;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CartResponseDTO {

    private Integer cartId;
    private String userEmail;
    private List<CartItemDTO> items;
    private double totalCartPrice;

public CartResponseDTO(ShoppingCart cart) {
    this.cartId = cart.getCartId();
    this.userEmail = cart.getCustomer().getEmail();
    this.items = cart.getCartItems().stream().map(cartItem ->
        new CartItemDTO(
            cartItem.getProduct().getProductId(),
            cartItem.getProduct().getName(),
            cartItem.getQuantity(),
            cartItem.getProduct().getPrice() * cartItem.getQuantity()
        )
    ).collect(Collectors.toList());
    this.totalCartPrice = this.items.stream().mapToDouble(CartItemDTO::getTotalPrice).sum();
}

    public CartResponseDTO(Integer cartId, String email, List<CartItemDTO> items, double totalPrice) {
        this.cartId = cartId;
        this.userEmail = email;
        this.items = items;
        this.totalCartPrice = totalPrice;
    }


    public CartResponseDTO(List<CartItem> cartItems, BigDecimal totalPrice) {
        this.items = cartItems.stream().map(cartItem ->
            new CartItemDTO(
                cartItem.getProduct().getProductId(),
                cartItem.getProduct().getName(),
                cartItem.getQuantity(),
                cartItem.getProduct().getPrice() * cartItem.getQuantity()
            )
        ).collect(Collectors.toList());
        this.totalCartPrice = totalPrice.doubleValue();
    }
}
