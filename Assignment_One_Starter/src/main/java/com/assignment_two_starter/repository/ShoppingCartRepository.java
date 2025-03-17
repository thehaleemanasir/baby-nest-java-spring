package com.assignment_two_starter.repository;

import com.assignment_two_starter.model.ShoppingCart;
import com.assignment_two_starter.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {
    Optional<ShoppingCart> findByCustomer(Customer customer);

    @Query("SELECT c FROM ShoppingCart c JOIN FETCH c.cartItems ci JOIN FETCH ci.product WHERE c.customer.email = :email AND c.active = :active")
    Optional<ShoppingCart> findByCustomerEmailAndActive(@Param("email") String email, @Param("active") boolean active);
}