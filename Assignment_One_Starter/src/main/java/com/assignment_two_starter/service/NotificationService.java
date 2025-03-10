package com.assignment_two_starter.service;

import com.assignment_two_starter.model.*;
import com.assignment_two_starter.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CustomerRepository customerRepository;
    public final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;
    private final WishlistProductRepository wishlistProductRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               CustomerRepository customerRepository,
                               ProductRepository productRepository,
                               WishlistRepository wishlistRepository, WishlistProductRepository wishlistProductRepository) {
        this.notificationRepository = notificationRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.wishlistRepository = wishlistRepository;
        this.wishlistProductRepository = wishlistProductRepository;
    }

    @Transactional
    public void checkPriceDrop(Integer productId) {
        Product updatedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        List<WishlistProduct> wishlistProducts = wishlistProductRepository.findByProduct(updatedProduct);

        for (WishlistProduct wishlistProduct : wishlistProducts) {
            Customer customer = wishlistProduct.getWishlist().getCustomer();
            String message = "Good news! The price of '" + updatedProduct.getName() + "' has dropped!";
            notificationRepository.save(new Notification(customer, message));
        }
    }

    @Transactional
    public void checkLowStock(Integer productId) {
        Product updatedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (updatedProduct.getStockQuantity() < 5) {
            List<WishlistProduct> wishlistProducts = wishlistProductRepository.findByProduct(updatedProduct);

            for (WishlistProduct wishlistProduct : wishlistProducts) {
                Customer customer = wishlistProduct.getWishlist().getCustomer();
                String message = "Hurry! '" + updatedProduct.getName() + "' is running low on stock (" + updatedProduct.getStockQuantity() + " left).";
                notificationRepository.save(new Notification(customer, message));
            }
        }
    }

    @Transactional
    public List<Notification> getUnreadNotifications(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Notification> notifications = notificationRepository.findByCustomerAndIsReadFalseOrderByCreatedAtDesc(customer);

        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);

        return notifications;
    }



    @Transactional
    public void markNotificationsAsRead(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Notification> notifications = notificationRepository.findByCustomerAndIsReadFalseOrderByCreatedAtDesc(customer);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }
}
