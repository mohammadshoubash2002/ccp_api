package com.mohammadshoubash.ccp_api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Customer customer;

    @ElementCollection
    private List<Order> orders;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal total;

    private String createdAt;

    public Order() {
    }

    public Order(Long id, Customer customer, List<Order> orders, OrderStatus status, BigDecimal total, String createdAt) {
        this.id = id;
        this.customer = customer;
        this.orders = orders;
        this.status = status;
        this.total = total;
        this.createdAt = createdAt;
    }

    public Long getId() { return this.id; }
    public Customer getCustomer() { return this.customer; }
    public OrderStatus getStatus() { return this.status; }
    public List<Order> getOrders() { return this.orders; }
    public BigDecimal getTotal() { return this.total; }
    public String getCreatedAt() { return this.createdAt; }
    
    public void setOrders(List<Order> orders) { this.orders = orders; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setTotal(BigDecimal total) { this.total = total; }

    @PrePersist
    public void setCreatedAt() { createdAt = LocalDateTime.now().toString(); }
}