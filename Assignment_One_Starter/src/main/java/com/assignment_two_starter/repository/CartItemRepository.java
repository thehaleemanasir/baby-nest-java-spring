package com.assignment_two_starter.repository;

import com.assignment_two_starter.model.CartItem;
import com.assignment_two_starter.model.Product;
import com.assignment_two_starter.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCart(ShoppingCart cart);


    Optional<CartItem> findByCartAndProduct(ShoppingCart cart, Product product);

}
