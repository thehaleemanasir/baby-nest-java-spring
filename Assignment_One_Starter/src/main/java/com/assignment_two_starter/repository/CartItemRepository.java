package com.assignment_two_starter.repository;

import com.assignment_two_starter.model.CartItem;
import com.assignment_two_starter.model.Product;
import com.assignment_two_starter.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCart(ShoppingCart cart);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = :cartId")
    List<CartItem> findByCartId(@Param("cartId") Long cartId);

    Optional<CartItem> findByCartAndProduct(ShoppingCart cart, Product product);

}