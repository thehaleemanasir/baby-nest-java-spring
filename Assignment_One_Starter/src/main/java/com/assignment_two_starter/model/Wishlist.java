package com.assignment_two_starter.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "wishlist")
public class Wishlist {

    @Getter
    @Setter
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Customer customer;

    @Getter
    @Setter
    @Column(nullable = false)
    private String wishlistName;

    @Getter
    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishlistProduct> wishlistProducts = new ArrayList<>();

    @Setter
    @Getter
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    @Setter
    @Column(unique = true, nullable = true)
    private String shareableLink;

    public String getShareableLink() {
        return shareableLink;
    }

    // Getters and Setters
}
