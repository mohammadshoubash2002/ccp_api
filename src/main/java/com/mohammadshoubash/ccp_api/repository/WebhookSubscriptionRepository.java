package com.mohammadshoubash.ccp_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mohammadshoubash.ccp_api.entity.WebhookSubscription;

@Repository 
public interface WebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, Long> {
    
    List<WebhookSubscription> findByEvent(String event);
}
