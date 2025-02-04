package com.assignment_two_starter.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 *
 * @author Alan.Ryan
 */

@Entity
@Table(name = "addresses")
@Data
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "address_id")
    private Integer addressId;

    @Basic(optional = false)
    @Column(name = "street_address")
    private String streetAddress;

    @Basic(optional = false)
    @Column(name = "city")
    private String city;

    @Basic(optional = false)
    @Column(name = "county")
    private String county;

    @Basic(optional = false)
    @Column(name = "postal_code")
    private String postalCode;

    @Basic(optional = false)
    @Column(name = "country")
    private String country;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne
    @ToString.Exclude
    private Customer userId;

    @OneToMany(mappedBy = "shippingAddressId")
    @ToString.Exclude
    private List<Orders> ordersList;

}
