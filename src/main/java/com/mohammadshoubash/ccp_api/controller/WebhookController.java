package com.mohammadshoubash.ccp_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mohammadshoubash.ccp_api.dto.MessageResponse;
import com.mohammadshoubash.ccp_api.dto.WebhookSubscriptionRequest;
import com.mohammadshoubash.ccp_api.entity.WebhookSubscription;
import com.mohammadshoubash.ccp_api.repository.WebhookSubscriptionRepository;
import com.mohammadshoubash.ccp_api.service.WebhookService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {
    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final WebhookSubscriptionRepository subscriptionRepository;
    private final WebhookService webhookService;

    public WebhookController(WebhookSubscriptionRepository subscriptionRepository, WebhookService webhookService) {
        this.subscriptionRepository = subscriptionRepository;
        this.webhookService = webhookService;
    }

    @PostMapping("/subscriptions")
    @ResponseStatus(HttpStatus.CREATED)
    public WebhookSubscription subscribe(@Valid @RequestBody WebhookSubscriptionRequest request) {
        WebhookSubscription sub = new WebhookSubscription(request.url(), request.event());
        return subscriptionRepository.save(sub);
    }

    @PostMapping("/incoming/orders")
    public ResponseEntity<MessageResponse> receiveOrderWebhook(
            @RequestHeader(value = "X-Digitinary-Signature", required = false) String signature,
            @RequestBody String rawPayload) {

        log.info("Received incoming webhook payload: {}", rawPayload);
        log.info("Received Signature Header: {}", signature);

        if (signature == null || !webhookService.verifySignature(rawPayload, signature)) {
            log.warn("Invalid webhook signature!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid HMAC signature"));
        }

        log.info("Webhook HMAC signature successfully verified!");
        return ResponseEntity.ok(new MessageResponse("Webhook received successfully"));
    }
}
