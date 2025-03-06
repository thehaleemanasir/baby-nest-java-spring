package com.assignment_two_starter.repository;

import com.assignment_two_starter.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.assignment_two_starter.model.OrderItems;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItems, Integer> {
    List<OrderItems> findByOrder(Orders order);
}