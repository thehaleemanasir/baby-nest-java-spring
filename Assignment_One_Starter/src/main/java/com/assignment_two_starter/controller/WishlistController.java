package com.assignment_two_starter.controller;

import com.assignment_two_starter.model.Wishlist;
import com.assignment_two_starter.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }


    @PostMapping("/add")
    public ResponseEntity<?> addToWishlist(@RequestBody Map<String, Object> request) {
        Integer userId = Integer.parseInt(request.get("userId").toString());
        Integer productId = Integer.parseInt(request.get("productId").toString());
        String note = request.get("note").toString();

        String response = wishlistService.addToWishlist(userId, productId, note);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove/{wishlistId}")
    public ResponseEntity<?> removeFromWishlist(@PathVariable Long wishlistId) {
        String response = wishlistService.removeFromWishlist(wishlistId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Wishlist>> getCustomerWishlist(@PathVariable Integer userId) {
        List<Wishlist> wishlist = wishlistService.getCustomerWishlist(userId);
        return ResponseEntity.ok(wishlist);
    }

    @PutMapping("/update-note")
    public ResponseEntity<?> updateWishlistNote(@RequestBody Map<String, Object> request) {
        Long wishlistId = Long.parseLong(request.get("wishlistId").toString());
        String note = request.get("note").toString();

        String response = wishlistService.updateWishlistNote(wishlistId, note);
        return ResponseEntity.ok(response);
    }
}
