package com.assignment_two_starter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "products")
@Data
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "product_id")
    private Integer productId;

    @Basic(optional = false)
    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    // @Max(value=?)  @Min(value=?)
    @Basic(optional = false)
    @Column(name = "price")
    private Double price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "feature_image")
    private String feature_image;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    @ToString.Exclude
    private List<Review> reviewList;

    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItemList;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    @ToString.Exclude
    private List<OrderItems> orderItemsList;

    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    @ManyToOne
    @ToString.Exclude
    private Category category;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived = false;

    public void toggleArchiveStatus() {
        this.isArchived = !this.isArchived;
    }

    public Integer getId() {
        return productId;
    }

    public void setId(Integer id) {
        this.productId = id;
    }

    public String getProductName() {
        return name;
    }

    public boolean isActive() {
        return !this.isArchived;
    }


}
