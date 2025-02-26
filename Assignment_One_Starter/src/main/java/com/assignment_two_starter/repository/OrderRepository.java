package com.assignment_two_starter.repository;

import com.assignment_two_starter.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Integer> {
    Optional<Orders> findById(Integer orderId);
}
