package com.assignment_two_starter.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    // Getters and Setters
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Customer customer;

    @Getter
    public  String message;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Setter
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    public Notification(Customer customer, String message) {
        this.customer = customer;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    public void setRead(boolean read) {
        isRead = read;
    }


    public boolean isRead() {
        return isRead;
    }

    public Serializable getCreatedAt() {
        return createdAt;
    }
}
