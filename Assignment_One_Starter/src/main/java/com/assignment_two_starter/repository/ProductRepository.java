package com.assignment_two_starter.repository;

import com.assignment_two_starter.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE p.isArchived = false")
    List<Product> findActiveProducts();
}
