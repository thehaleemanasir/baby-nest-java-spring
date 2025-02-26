package com.assignment_two_starter.repository;

import com.assignment_two_starter.model.Wishlist;
import com.assignment_two_starter.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByCustomer(Customer customer);
}
