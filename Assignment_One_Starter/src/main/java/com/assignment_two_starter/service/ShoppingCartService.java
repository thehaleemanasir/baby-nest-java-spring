package com.assignment_two_starter.service;

import com.assignment_two_starter.dto.AddToCartRequestDTO;
import com.assignment_two_starter.dto.CartItemDTO;
import com.assignment_two_starter.dto.CartResponseDTO;
import com.assignment_two_starter.dto.UpdateCartRequestDTO;
import com.assignment_two_starter.model.*;
import com.assignment_two_starter.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, CustomerRepository customerRepository,
                               ProductRepository productRepository, CartItemRepository cartItemRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }


   @Transactional
public CartResponseDTO getActiveCartForCustomer(String email) {
    Customer customer = customerRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


    ShoppingCart cart = shoppingCartRepository.findByCustomerEmailAndActive(email, true)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No active cart found"));

       if (cart.getCartItems() == null) {
           cart.setCartItems(new ArrayList<>());
       }

    List<CartItemDTO> items = cart.getCartItems().stream().map(item ->
        new CartItemDTO(
            item.getProduct().getProductId(),
            item.getProduct().getName(),
            item.getQuantity(),
            item.getProduct().getPrice() * item.getQuantity()
        )
    ).collect(Collectors.toList());

    double totalPrice = items.stream().mapToDouble(CartItemDTO::getPrice).sum();
    totalPrice = Double.parseDouble(String.format("%.2f", totalPrice));
       return new CartResponseDTO(cart.getCartId(), customer.getEmail(), items, totalPrice);
}
    @Transactional
    public CartResponseDTO addToCart(String email, AddToCartRequestDTO request) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));


        if (!product.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found.");
        }

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock available.");
        }


        ShoppingCart cart = shoppingCartRepository.findByCustomer(customer)
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setCustomer(customer);
                    newCart.setCreatedAt(new Date());
                    newCart.setActive(true);
                    shoppingCartRepository.save(newCart);
                    return newCart;
                });


        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
            cart.getCartItems().add(cartItem);
        }

        shoppingCartRepository.save(cart);
        return new CartResponseDTO(cart);
    }

@Transactional
public CartResponseDTO updateCartItem(String email, UpdateCartRequestDTO requestDTO) {
    Customer customer = customerRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    ShoppingCart cart = shoppingCartRepository.findByCustomer(customer)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No active cart found"));

    CartItem cartItem = cart.getCartItems().stream()
            .filter(item -> item.getProduct().getProductId().equals(requestDTO.getProductId()))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found in cart"));

    Product product = cartItem.getProduct();


    if (requestDTO.getNewQuantity() < 1) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be at least 1");
    }
    if (requestDTO.getNewQuantity() > product.getStockQuantity()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Requested quantity exceeds available stock. Only " + product.getStockQuantity() + " left.");
    }

    cartItem.setQuantity(requestDTO.getNewQuantity());

    BigDecimal totalPrice = cart.getCartItems().stream()
        .map(item -> BigDecimal.valueOf(item.getProduct().getPrice()).multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, BigDecimal.ROUND_HALF_UP);

    shoppingCartRepository.save(cart);



    return new CartResponseDTO(cart.getCartItems(), totalPrice);
}

    @Transactional
public CartResponseDTO removeCartItem(String email, Integer productId) {
    Customer customer = customerRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    ShoppingCart cart = shoppingCartRepository.findByCustomerEmailAndActive(email, true)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No active cart found"));

    CartItem cartItem = cart.getCartItems().stream()
            .filter(item -> item.getProduct().getProductId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found in cart"));

    cart.getCartItems().remove(cartItem);
    cartItemRepository.delete(cartItem);
    shoppingCartRepository.save(cart);

    List<CartItemDTO> items = cart.getCartItems().stream().map(item -> {
        CartItemDTO dto = new CartItemDTO(
                item.getProduct().getProductId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getProduct().getPrice() * item.getQuantity()
        );
        dto.setProductId(item.getProduct().getProductId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getProduct().getPrice() * item.getQuantity());
        return dto;
    }).collect(Collectors.toList());

        BigDecimal totalPrice = items.stream()
            .map(CartItemDTO::getPrice)
            .map(BigDecimal::valueOf)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, BigDecimal.ROUND_HALF_UP);

        return new CartResponseDTO(cart.getCartId(), customer.getEmail(), items, totalPrice.doubleValue());
    }

    @Transactional
    public void clearCart(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ShoppingCart cart = shoppingCartRepository.findByCustomer(customer)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No active cart found"));


        cart.getCartItems().clear();


        cart.setActive(false);


        shoppingCartRepository.save(cart);
    }

}
