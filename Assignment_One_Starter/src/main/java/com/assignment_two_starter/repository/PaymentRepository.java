package com.assignment_two_starter.repository;

import com.assignment_two_starter.model.Orders;
import com.assignment_two_starter.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Object> findByOrder(Orders order);
}
