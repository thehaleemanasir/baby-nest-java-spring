package com.assignment_two_starter.service;

import com.assignment_two_starter.model.Product;
import com.assignment_two_starter.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public ProductService(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    public List<Product> findActiveProducts() {
        return productRepository.findActiveProducts();
    }

    public Product findProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Integer productId, Product updatedProduct) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        boolean priceDropped = updatedProduct.getPrice() < existingProduct.getPrice();
        boolean lowStock = updatedProduct.getStockQuantity() < 5;

        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStockQuantity(updatedProduct.getStockQuantity());

        Product savedProduct = productRepository.save(existingProduct);

        if (priceDropped) {
            notificationService.checkPriceDrop(productId);
        }

        if (lowStock) {
            notificationService.checkLowStock(productId);
        }

        return savedProduct;
    }



    public boolean deleteProduct(Integer id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
