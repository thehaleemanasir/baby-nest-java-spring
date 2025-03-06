package com.assignment_two_starter.service;

import com.assignment_two_starter.model.Orders;
import com.assignment_two_starter.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderServiceCustomer {

    private final OrderRepository orderRepository;


    public OrderServiceCustomer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }

    public Orders getOrderById(Integer orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public Orders createOrder(Orders order) {
        return orderRepository.save(order);
    }

    public Orders updateOrder(Integer orderId, Orders updatedOrder) {
        Optional<Orders> existingOrder = orderRepository.findById(orderId);
        if (existingOrder.isPresent()) {
            Orders order = existingOrder.get();
            order.setTotalAmount(updatedOrder.getTotalAmount());
            order.setStatus(updatedOrder.getStatus());
            return orderRepository.save(order);
        }
        return null;
    }

    public boolean deleteOrder(Integer orderId) {
        if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId);
            return true;
        }
        return false;
    }
}
