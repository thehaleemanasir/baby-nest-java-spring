package com.assignment_two_starter.service;

import com.assignment_two_starter.dto.ClearWishlistRequestDTO;
import com.assignment_two_starter.dto.RemoveWishlistItemRequestDTO;
import com.assignment_two_starter.dto.WishlistRequestDTO;
import com.assignment_two_starter.dto.WishlistResponseDTO;
import com.assignment_two_starter.model.Wishlist;
import com.assignment_two_starter.model.Customer;
import com.assignment_two_starter.model.Product;
import com.assignment_two_starter.model.WishlistProduct;
import com.assignment_two_starter.repository.WishlistRepository;
import com.assignment_two_starter.repository.CustomerRepository;
import com.assignment_two_starter.repository.ProductRepository;
import com.assignment_two_starter.repository.WishlistProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistProductRepository wishlistProductRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public WishlistService(WishlistRepository wishlistRepository, WishlistProductRepository wishlistProductRepository,
                           CustomerRepository customerRepository, ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistProductRepository = wishlistProductRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public WishlistResponseDTO createWishlist(String email, WishlistRequestDTO requestDTO) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        Optional<Wishlist> existingWishlist = wishlistRepository.findByCustomerAndWishlistName(customer, requestDTO.getWishlistName());
        if (existingWishlist.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wishlist with this name already exists.");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setCustomer(customer);
        wishlist.setWishlistName(requestDTO.getWishlistName());
        wishlist.setCreatedAt(LocalDateTime.now());

        wishlistRepository.save(wishlist);

        return new WishlistResponseDTO(wishlist);
    }

    public List<WishlistResponseDTO> getWishlists(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        return wishlistRepository.findByCustomer(customer)
                .stream()
                .map(WishlistResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public WishlistResponseDTO addToWishlist(String email, WishlistRequestDTO requestDTO) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        Wishlist wishlist = wishlistRepository.findByCustomerAndWishlistName(customer, requestDTO.getWishlistName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wishlist not found"));

        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found"));

        boolean productExists = wishlist.getWishlistProducts().stream()
                .anyMatch(wp -> wp.getProduct().getId().equals(requestDTO.getProductId()));

        if (productExists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is already in the wishlist.");
        }

        WishlistProduct wishlistProduct = new WishlistProduct();
        wishlistProduct.setWishlist(wishlist);
        wishlistProduct.setProduct(product);
        wishlistProduct.setNote(requestDTO.getNote());

        wishlistProductRepository.save(wishlistProduct);

        return new WishlistResponseDTO(wishlist);
    }

    @Transactional
    public WishlistResponseDTO removeItemFromWishlist(String email, RemoveWishlistItemRequestDTO requestDTO) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        Wishlist wishlist = wishlistRepository.findByCustomerAndWishlistName(customer, requestDTO.getWishlistName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wishlist not found"));


        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found"));


        WishlistProduct wishlistProduct = wishlistProductRepository.findByWishlistAndProduct(wishlist, product)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not in the wishlist"));


        wishlist.getWishlistProducts().remove(wishlistProduct);

        wishlistProductRepository.delete(wishlistProduct);

        wishlistRepository.save(wishlist);

        return new WishlistResponseDTO(wishlist);
    }


    @Transactional
    public WishlistResponseDTO clearWishlist(String email, ClearWishlistRequestDTO requestDTO) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        Wishlist wishlist = wishlistRepository.findByCustomerAndWishlistName(customer, requestDTO.getWishlistName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wishlist not found"));


        wishlistProductRepository.deleteByWishlist(wishlist);

        wishlist.getWishlistProducts().clear();
        wishlistRepository.save(wishlist);

        return new WishlistResponseDTO(wishlist);
    }

    @Transactional
    public void deleteWishlist(String email, String wishlistName) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        Wishlist wishlist = wishlistRepository.findByCustomerAndWishlistName(customer, wishlistName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wishlist not found"));

        wishlistProductRepository.deleteByWishlist(wishlist);

        wishlistRepository.delete(wishlist);
    }

    public String generateShareableLink(String email, String wishlistName) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        Wishlist wishlist = wishlistRepository.findByCustomerAndWishlistName(customer, wishlistName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wishlist not found"));

        String baseUrl = "http://localhost:8070";

        String shareableLink = baseUrl + "/wishlist/view/" + wishlist.getId();

        wishlist.setShareableLink(shareableLink);
        wishlistRepository.save(wishlist);

        return shareableLink;
    }


}
