package com.assignment_two_starter.service;

import com.assignment_two_starter.model.Wishlist;
import com.assignment_two_starter.model.Customer;
import com.assignment_two_starter.model.Product;
import com.assignment_two_starter.repository.WishlistRepository;
import com.assignment_two_starter.repository.CustomerRepository;
import com.assignment_two_starter.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public WishlistService(WishlistRepository wishlistRepository, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    public String addToWishlist(Integer userId, Integer productId, String note) {
        Optional<Customer> customer = customerRepository.findById(userId);
        Optional<Product> product = productRepository.findById(productId);

        if (customer.isEmpty() || product.isEmpty()) {
            return "Customer or Product not found.";
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setCustomer(customer.get());
        wishlist.setProduct(product.get());
        wishlist.setNote(note);
        wishlistRepository.save(wishlist);

        return "Product added to wishlist.";
    }

    public String removeFromWishlist(Long wishlistId) {
        if (wishlistRepository.existsById(wishlistId)) {
            wishlistRepository.deleteById(wishlistId);
            return "Product removed from wishlist.";
        }
        return "Wishlist item not found.";
    }


    public List<Wishlist> getCustomerWishlist(Integer userId) {
        Optional<Customer> customer = customerRepository.findById(userId);
        return customer.map(wishlistRepository::findByCustomer).orElse(null);
    }


    public String updateWishlistNote(Long wishlistId, String newNote) {
        Optional<Wishlist> wishlist = wishlistRepository.findById(wishlistId);
        if (wishlist.isPresent()) {
            Wishlist item = wishlist.get();
            item.setNote(newNote);
            wishlistRepository.save(item);
            return "Wishlist item updated.";
        }
        return "Wishlist item not found.";
    }
}
