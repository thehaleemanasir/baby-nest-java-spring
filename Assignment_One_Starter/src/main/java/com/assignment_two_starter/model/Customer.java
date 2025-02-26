package com.assignment_two_starter.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "users")
@Data

public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @OneToMany(mappedBy = "customer")
    private List<ShoppingCart> shoppingCartList;

    @OneToMany(mappedBy = "userId")
    private List<Address> addressesList;

    @OneToMany(mappedBy = "customer")
    private List<Review> reviewList;

    @OneToMany(mappedBy = "customer")
    private List<Orders> ordersList;

}
