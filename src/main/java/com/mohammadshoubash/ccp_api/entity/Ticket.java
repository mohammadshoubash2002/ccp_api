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
    private AppUser user;

    private String subject;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    private String createdAt;

    public Ticket() {}

    public Ticket(Long id, AppUser user, String subject, TicketStatus status, TicketPriority priority, String createdAt) {
        this.id = id;
        this.user = user;
        this.subject = subject;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public AppUser getCustomer() { return user; }
    public String getSubject() { return subject; }
    public TicketStatus getStatus() { return status; }
    public TicketPriority getPriority() { return priority; }
    public String getCreatedAt() { return createdAt; }
    
    public void setSubject(String subject) { this.subject = subject; }
    public void setUser(AppUser user) { this.user = user; }
    public void setCustomer(AppUser user) { this.user = user; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public void setPriority(TicketPriority priority) { this.priority = priority; }

    @PrePersist
    public void setCreatedAt() { createdAt = LocalDateTime.now().toString(); }
}