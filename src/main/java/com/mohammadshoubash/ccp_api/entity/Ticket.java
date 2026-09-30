package com.mohammadshoubash.ccp_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private String subject;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    private String createdAt;

    public Ticket() {}

    public Ticket(Long id, Customer customer, String subject, TicketStatus status, TicketPriority priority, String createdAt) {
        this.id = id;
        this.customer = customer;
        this.subject = subject;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public String getSubject() { return subject; }
    public TicketStatus getStatus() { return status; }
    public TicketPriority getPriority() { return priority; }
    public String getCreatedAt() { return createdAt; }
    
    public void setSubject(String subject) { this.subject = subject; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public void setPriority(TicketPriority priority) { this.priority = priority; }

    @PrePersist
    public void setCreatedAt() { createdAt = LocalDateTime.now().toString(); }
}