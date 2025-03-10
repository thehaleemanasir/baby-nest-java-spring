package com.assignment_two_starter.repository;

import com.assignment_two_starter.model.Wishlist;
import com.assignment_two_starter.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    List<Wishlist> findByCustomer(Customer customer);
    Optional<Wishlist> findByCustomerAndWishlistName(Customer customer, String wishlistName);
}
