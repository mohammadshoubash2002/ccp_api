package com.mohammadshoubash.ccp_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

@Entity
@Table(name = "webhook_subscriptions")
public class WebhookSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String event;

    public WebhookSubscription() {}

    public WebhookSubscription(String url, String event) {
        this.url = url;
        this.event = event;
    }

    public Long getId() { return id; }
    public String getUrl() { return url; }
    public String getEvent() { return event; }
    public void setUrl(String url) { this.url = url; }
    public void setEvent(String event) { this.event = event; }
}
