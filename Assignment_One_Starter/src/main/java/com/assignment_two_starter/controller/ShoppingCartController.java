package com.assignment_two_starter.controller;

import com.assignment_two_starter.model.CartItem;
import com.assignment_two_starter.model.Customer;
import com.assignment_two_starter.model.Product;
import com.assignment_two_starter.model.ShoppingCart;
import com.assignment_two_starter.repository.CustomerRepository;
import com.assignment_two_starter.repository.ProductRepository;
import com.assignment_two_starter.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService, ProductRepository productRepository, CustomerRepository customerRepository) {
        this.shoppingCartService = shoppingCartService;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getCart(@PathVariable Integer userId) {


        Optional<Customer> userOptional = customerRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "User not found",
                            "message", "No user exists with the given ID"
                    ));
        }

        Customer user = userOptional.get(); // Fetch user details


        ShoppingCart cart = shoppingCartService.getActiveCartForCustomer(userId);


        if (cart == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "No active cart",
                            "message", "The user does not have an active cart"
                    ));
        }


        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("cartId", cart.getCartId());
        response.put("userEmail", user.getEmail()); // Replace userId with email

        List<Map<String, Object>> items = cart.getCartItems().stream().map(item -> {
            Map<String, Object> itemMap = new LinkedHashMap<>();
            itemMap.put("productId", item.getProduct().getProductId());
            itemMap.put("quantity", item.getQuantity());
            return itemMap;
        }).collect(Collectors.toList());

        response.put("items", items);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/add")
    public ResponseEntity<Object> addToCart(@RequestBody Map<String, Object> payload) {

        if (!payload.containsKey("userId") || !payload.containsKey("productId") || !payload.containsKey("quantity")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid request. 'userId', 'productId', and 'quantity' are required."));
        }

        Integer userId = (Integer) payload.get("userId");
        Integer productId = (Integer) payload.get("productId");
        Integer quantity = (Integer) payload.get("quantity");

        if (userId == null || productId == null || quantity == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid data. User ID, Product ID, and Quantity cannot be null."));
        }


        Optional<Customer> userOptional = customerRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found."));
        }
        Customer user = userOptional.get();


        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Product not found."));
        }
        Product product = productOptional.get();


        if (quantity > product.getStockQuantity()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Requested quantity exceeds available stock. Only " + product.getStockQuantity() + " left."));
        }

        ShoppingCart cart = shoppingCartService.addToCart(userId, productId, quantity);


        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems == null) {
            cartItems = new ArrayList<>(); // Assign empty list to prevent null errors
        }


        Map<String, Object> response = new HashMap<>();
        response.put("cartId", cart.getCartId());
        response.put("userEmail", user.getEmail()); // Replace userId with email

        List<Map<String, Object>> items = cartItems.stream().map(item -> {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("productId", item.getProduct().getProductId());
            itemMap.put("quantity", item.getQuantity());
            return itemMap;
        }).collect(Collectors.toList());

        response.put("items", items);

        return ResponseEntity.ok(response);
    }




    @DeleteMapping("/remove")
    public ResponseEntity<String> removeCartItem(@RequestBody Map<String, Integer> requestBody) {
        Integer userId = requestBody.get("userId");
        Integer productId = requestBody.get("productId");

        if (userId == null || productId == null) {
            return ResponseEntity.badRequest().body("Both userId and productId are required.");
        }

        shoppingCartService.removeCartItem(userId, productId);
        return ResponseEntity.ok("Item removed from cart successfully.");
    }

    @PutMapping("/{userId}/update/{productId}")
    public ResponseEntity<Map<String, Object>> updateCartItem(
            @PathVariable Integer userId,
            @PathVariable Integer productId,
            @RequestBody Map<String, Integer> requestBody) {


        if (!requestBody.containsKey("quantity")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "error", "Missing required field: 'quantity'",
                            "message", "Please provide a valid quantity in the request body"
                    ));
        }

        int requestedQuantity = requestBody.get("quantity");


        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Product not found",
                            "message", "The requested product does not exist"
                    ));
        }

        Product product = productOptional.get();


        if (requestedQuantity > product.getStockQuantity()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "error", "Requested quantity exceeds available stock",
                            "message", "Only " + product.getStockQuantity() + " left in stock",
                            "availableStock", product.getStockQuantity()
                    ));
        }


        CartItem updatedItem = shoppingCartService.updateCartItem(userId, productId, requestedQuantity);


        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Cart item updated successfully");

        Map<String, Object> cartItemDetails = new LinkedHashMap<>();
        cartItemDetails.put("productId", updatedItem.getProduct().getProductId());
        cartItemDetails.put("updatedQuantity", updatedItem.getQuantity());

        response.put("updatedCartItem", cartItemDetails);

        return ResponseEntity.ok(response);
    }



    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Integer userId) {
        shoppingCartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
