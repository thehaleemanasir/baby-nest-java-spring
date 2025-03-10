package com.assignment_two_starter.repository;

import com.assignment_two_starter.model.Notification;
import com.assignment_two_starter.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByCustomerAndIsReadFalseOrderByCreatedAtDesc(Customer customer);
}
