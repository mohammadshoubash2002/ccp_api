package com.mohammadshoubash.ccp_api.service;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.mohammadshoubash.ccp_api.dto.WebhookEventPayload;
import com.mohammadshoubash.ccp_api.entity.WebhookSubscription;
import com.mohammadshoubash.ccp_api.repository.WebhookSubscriptionRepository;

import tools.jackson.databind.ObjectMapper;

@Service
public class WebhookService {
    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final WebhookSubscriptionRepository subscriptionRepository;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final String webhookSecret;

    public WebhookService(WebhookSubscriptionRepository subscriptionRepository,
                          ObjectMapper objectMapper,
                          @Value("${app.webhook.secret}") String webhookSecret) {
        this.subscriptionRepository = subscriptionRepository;
        this.objectMapper = objectMapper;
        this.webhookSecret = webhookSecret;
        this.restClient = RestClient.create();
    }

    /**
     * Fired asynchronously without blocking the order status update.
     */
    @Async
    public void publishEvent(String event, WebhookEventPayload payload) {
        List<WebhookSubscription> subscriptions = subscriptionRepository.findByEvent(event);

        if (subscriptions.isEmpty()) {
            log.info("No subscribers found for event: {}", event);
            return;
        }

        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            String signature = calculateHmac(jsonPayload, webhookSecret);

            for (WebhookSubscription sub : subscriptions) {
                try {
                    log.info("Sending webhook [{}] to {}", event, sub.getUrl());
                    restClient.post()
                            .uri(sub.getUrl())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Digitinary-Signature", signature)
                            .body(jsonPayload)
                            .retrieve()
                            .toBodilessEntity();
                    log.info("Successfully delivered webhook to {}", sub.getUrl());
                } catch (Exception e) {
                    log.error("Failed to deliver webhook to {}: {}", sub.getUrl(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to serialize webhook payload: {}", e.getMessage());
        }
    }

    public String calculateHmac(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return "sha256=" + HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Could not calculate HMAC", e);
        }
    }

    public boolean verifySignature(String payload, String receivedHeader) {
        String expectedSignature = calculateHmac(payload, webhookSecret);
        return MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8),
                                     receivedHeader.getBytes(StandardCharsets.UTF_8));
    }
}
