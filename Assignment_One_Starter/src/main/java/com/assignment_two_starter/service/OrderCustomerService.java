package com.assignment_two_starter.service;

import com.assignment_two_starter.dto.OrderItemCustomerDTO;
import com.assignment_two_starter.dto.OrderRequestDTO;
import com.assignment_two_starter.dto.OrderResponseDTO;
import com.assignment_two_starter.model.*;
import com.assignment_two_starter.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderCustomerService {

    private final OrderRepository ordersRepository;
    private final OrderItemRepository orderItemsRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final AddressRepository addressRepository;

    public OrderCustomerService(
            OrderRepository ordersRepository,
            OrderItemRepository orderItemsRepository,
            PaymentRepository paymentRepository,
            CustomerRepository customerRepository,
            ShoppingCartRepository shoppingCartRepository,
            AddressRepository addressRepository) {
        this.ordersRepository = ordersRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public OrderResponseDTO placeOrder(String email, OrderRequestDTO requestDTO) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Optional<ShoppingCart> optionalCart = shoppingCartRepository.findByCustomerEmailAndActive(email, true);

        if (optionalCart.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No active cart found for user: " + email);
        }

        ShoppingCart cart = optionalCart.get();

        if (cart.getCartItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot place an order with an empty cart. User: " + email);
        }

        Address address = addressRepository.findByStreetAddressAndCityAndPostalCode(
                        requestDTO.getShippingAddress(), requestDTO.getCity(), requestDTO.getPostalCode())
                .orElseGet(() -> {
                    Address newAddress = new Address();
                    newAddress.setStreetAddress(requestDTO.getShippingAddress());
                    newAddress.setCity(requestDTO.getCity());
                    newAddress.setCounty(requestDTO.getCounty());
                    newAddress.setPostalCode(requestDTO.getPostalCode());
                    newAddress.setCountry(requestDTO.getCountry());
                    newAddress.setUserId(customer);
                    return addressRepository.save(newAddress);
                });


        Orders order = new Orders();
        order.setCustomer(customer);
        order.setTotalAmount(cart.getTotalPrice());
        order.setEstimatedDeliveryDate(LocalDate.now().plusDays(5));
        order.setAddressChangeFee(BigDecimal.ZERO.doubleValue());
        order.setShippingAddress(address);
        order.setStatus("PENDING");

        Orders savedOrder = ordersRepository.save(order);

        List<OrderItemCustomerDTO> orderItemsDTOList = cart.getCartItems().stream().map(cartItem -> {
            OrderItems orderItem = new OrderItems();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());

            BigDecimal totalPrice = BigDecimal.valueOf(cartItem.getProduct().getPrice())
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            orderItem.setUnitPrice(BigDecimal.valueOf(cartItem.getProduct().getPrice()));
            orderItem.setTotalPrice(totalPrice);
            orderItemsRepository.save(orderItem);

            return new OrderItemCustomerDTO(orderItem);
        }).collect(Collectors.toList());

        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setPaymentMethod(requestDTO.getPaymentMethod());
        payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
        paymentRepository.save(payment);

        cart.setActive(false);
        shoppingCartRepository.save(cart);

        return new OrderResponseDTO(
                savedOrder.getOrderId(),
                customer.getEmail(),
                requestDTO.getShippingAddress(),
                orderItemsDTOList,
                savedOrder.getTotalAmount(),
                "PENDING",
                "PENDING"
        );
    }


    @Transactional
    public OrderResponseDTO editOrder(Long orderId, OrderRequestDTO orderRequest, String userEmail) {
        Orders order = ordersRepository.findById(Math.toIntExact(orderId))
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        if (!order.getCustomer().getEmail().equals(userEmail)) {
            throw new IllegalStateException("You are not authorized to edit this order");
        }

        if (!order.getStatus().equalsIgnoreCase("PENDING")) {
            throw new IllegalStateException("Order cannot be edited because it is not in PENDING status");
        }

        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setCity(orderRequest.getCity());
        order.setCounty(orderRequest.getCounty());
        order.setPostalCode(orderRequest.getPostalCode());
        order.setCountry(orderRequest.getCountry());
        order.setPaymentMethod(orderRequest.getPaymentMethod());

        return new OrderResponseDTO(order);
    }
}
