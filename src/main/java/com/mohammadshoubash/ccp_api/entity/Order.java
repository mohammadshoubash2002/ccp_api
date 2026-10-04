package com.mohammadshoubash.ccp_api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private AppUser user;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal total;

    private String createdAt;

    public Order() {
    }

    public Order(Long id, AppUser user, OrderStatus status, BigDecimal total, String createdAt) {
        this.id = id;
        this.user = user;
        this.status = status;
        this.total = total;
        this.createdAt = createdAt;
    }

    public Long getId() { return this.id; }
    public AppUser getUser() { return this.user; }
    public void setUser(AppUser user) { this.user = user; }
    public AppUser getCustomer() { return this.user; }
    public void setCustomer(AppUser user) { this.user = user; }
    public OrderStatus getStatus() { return this.status; }
    public BigDecimal getTotal() { return this.total; }
    public String getCreatedAt() { return this.createdAt; }
    
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setTotal(BigDecimal total) { this.total = total; }

    @PrePersist
    public void setCreatedAt() { createdAt = LocalDateTime.now().toString(); }
}