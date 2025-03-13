package com.assignment_two_starter.repository;

import com.assignment_two_starter.model.ShoppingCart;
import com.assignment_two_starter.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {
    Optional<ShoppingCart> findByCustomer(Customer customer);
    Optional<ShoppingCart> findByCustomerEmailAndActive(String email, boolean active);

}
