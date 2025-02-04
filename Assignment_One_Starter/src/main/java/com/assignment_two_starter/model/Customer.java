package com.assignment_two_starter.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @author Alan.Ryan
 */
@Entity
@Table(name = "users")
@Data
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "user_id")
    private Integer userId;

    @Basic(optional = false)
    @Column(name = "first_name")
    private String firstName;

    @Basic(optional = false)
    @Column(name = "last_name")
    private String lastName;

    @Basic(optional = false)
    @Column(name = "email")
    private String email;

    @Basic(optional = false)
    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    @ToString.Exclude
    private List<ShoppingCart> shoppingCartList;

    @OneToMany(mappedBy = "userId")
    @ToString.Exclude
    private List<Address> addressesList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    @ToString.Exclude
    private List<Review> reviewList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    @ToString.Exclude
    private List<Orders> ordersList;

}
