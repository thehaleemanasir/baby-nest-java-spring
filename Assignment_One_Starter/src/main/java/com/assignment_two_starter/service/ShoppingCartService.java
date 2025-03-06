package com.assignment_two_starter.service;

import com.assignment_two_starter.model.CartItem;
import com.assignment_two_starter.model.Customer;
import com.assignment_two_starter.model.Product;
import com.assignment_two_starter.model.ShoppingCart;
import com.assignment_two_starter.repository.CartItemRepository;
import com.assignment_two_starter.repository.CustomerRepository;
import com.assignment_two_starter.repository.ProductRepository;
import com.assignment_two_starter.repository.ShoppingCartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository,
                               CustomerRepository customerRepository,
                               ProductRepository productRepository,
                               CartItemRepository cartItemRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional
    public ShoppingCart getActiveCartForCustomer(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        return shoppingCartRepository.findByCustomerAndActive(customer, true)
                .orElseThrow(() -> new IllegalArgumentException("No active cart found for customer"));
    }

    @Transactional
    public ShoppingCart addToCart(Integer customerId, Integer productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        ShoppingCart cart = shoppingCartRepository.findByCustomerAndActive(customer, true)
                .orElseGet(() -> createNewCart(customer));

        CartItem cartItem = cartItemRepository.findByCart(cart).stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst()
                .orElseGet(() -> createNewCartItem(cart, product));

        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItemRepository.save(cartItem);

        return cart;
    }

    @Transactional
    public void clearCart(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        ShoppingCart cart = shoppingCartRepository.findByCustomerAndActive(customer, true)
                .orElseThrow(() -> new IllegalArgumentException("No active cart found"));

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cart.setActive(false);
        shoppingCartRepository.save(cart);
    }

    @Transactional
    public ShoppingCart createNewCart(Customer customer) {
        ShoppingCart cart = new ShoppingCart();
        cart.setCustomer(customer);
        cart.setActive(true);
        return shoppingCartRepository.save(cart);
    }

    @Transactional
    public CartItem createNewCartItem(ShoppingCart cart, Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(0);
        return cartItemRepository.save(cartItem);
    }

    @Transactional
    public void removeCartItem(Integer userId, Integer productId) {
        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        ShoppingCart cart = shoppingCartRepository.findByCustomerAndActive(customer, true)
                .orElseThrow(() -> new IllegalArgumentException("No active cart found for customer"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Optional<CartItem> cartItem = cartItemRepository.findByCartAndProduct(cart, product);
        cartItem.ifPresent(cartItemRepository::delete);
    }

   @Transactional
    public CartItem updateCartItem(Integer userId, Integer productId, int quantity) {

        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));


        ShoppingCart cart = shoppingCartRepository.findByCustomerAndActive(customer, true)
                .orElseThrow(() -> new IllegalArgumentException("No active cart found"));


        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));


        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));


        if (quantity > product.getStockQuantity()) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock. Only " + product.getStockQuantity() + " left.");
        }


        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }


}
