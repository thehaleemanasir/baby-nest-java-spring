package com.assignment_two_starter.repository;

import com.assignment_two_starter.model.Product;
import com.assignment_two_starter.model.Wishlist;
import com.assignment_two_starter.model.WishlistProduct;
import com.assignment_two_starter.model.WishlistProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistProductRepository extends JpaRepository<WishlistProduct, WishlistProductId> {

    Optional<WishlistProduct> findByWishlistAndProduct(Wishlist wishlist, Product product);
    void deleteByWishlistAndProduct(Wishlist wishlist, Product product);
    List<WishlistProduct> findByProduct(Product product);
    @Modifying
    @Query("DELETE FROM WishlistProduct wp WHERE wp.wishlist = :wishlist")
    void deleteByWishlist(@Param("wishlist") Wishlist wishlist);


}

