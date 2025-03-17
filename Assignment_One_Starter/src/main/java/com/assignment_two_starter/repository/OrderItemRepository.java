package com.assignment_two_starter.repository;

import com.assignment_two_starter.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.assignment_two_starter.model.OrderItems;
import java.util.List;
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItems, Long> {
    @Query("SELECT oi FROM OrderItems oi WHERE oi.order.orderId = :orderId")
    List<OrderItems> findByOrder(@Param("orderId") Long orderId);

    List<OrderItems> findByOrderOrderId(Long orderId);

}
