package com.assignment_two_starter.repository;

import com.assignment_two_starter.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
