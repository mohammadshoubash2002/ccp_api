package com.mohammadshoubash.ccp_api.service;

import com.mohammadshoubash.ccp_api.dto.OrderRequest;
import com.mohammadshoubash.ccp_api.dto.OrderResponse;
import com.mohammadshoubash.ccp_api.dto.WebhookEventPayload;
import com.mohammadshoubash.ccp_api.entity.Order;
import com.mohammadshoubash.ccp_api.entity.OrderStatus;
import com.mohammadshoubash.ccp_api.entity.Role;
import com.mohammadshoubash.ccp_api.exception.ResourceNotFoundException;
import com.mohammadshoubash.ccp_api.repository.OrderRepository;
import com.mohammadshoubash.ccp_api.repository.AppUserRepository;
import com.mohammadshoubash.ccp_api.entity.AppUser;
import com.mohammadshoubash.ccp_api.specification.OrderSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private WebhookService webhookService;

    public OrderResponse createOrder(OrderRequest orderRequest) {
        if (orderRequest == null) {
            throw new IllegalArgumentException("Order request cannot be null");
        }

        Order order = new Order();
        order.setStatus(OrderStatus.valueOf(orderRequest.status().toUpperCase()));
        
        AppUser user = appUserRepository.findById(orderRequest.customer_id())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + orderRequest.customer_id()));

        order.setUser(user);
        order.setTotal(orderRequest.total());

        order = orderRepository.save(order);

        return new OrderResponse(order);
    }

    public OrderResponse getOrderById(Long id, String username) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        AppUser currentUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        // Admin can view any order
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        
        // Customer can only view their own order
        boolean isOwner = order.getUser() != null 
                && order.getUser().getUsername().equals(username);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You don't have permission to view this order");
        }

        return new OrderResponse(order);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(OrderResponse::new).toList();
    }

    @PreAuthorize("hasRole('ADMIN') or #customerId == authentication.principal.id")
    public List<OrderResponse> getOrdersByCustomerId(Long customerId) {
        AppUser customer = appUserRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        return orderRepository.findByUserId(customer.getId()).stream().map(OrderResponse::new).toList();
    }

    public OrderResponse updateOrderStatus(Long id, OrderStatus status, String username) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        AppUser currentUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        // Admin can view any order
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        
        // Customer can only view their own order
        boolean isOwner = order.getUser() != null 
                && order.getUser().getUsername().equals(username);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You don't have permission to update this order");
        }

        OrderStatus previousStatus = order.getStatus();

        if (previousStatus != status) {
            order.setStatus(status);
            order = orderRepository.save(order);

            // Trigger webhook asynchronously
            webhookService.publishEvent("order.status_changed", new WebhookEventPayload(
                    "order.status_changed",
                    order.getId(),
                    previousStatus.name(),
                    status.name(),
                    java.time.Instant.now().toString()
            ));
        } else {
            throw new IllegalArgumentException("Order status is already " + status.name());
        }

        return new OrderResponse(order);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<Order> getOrdersByFilters(String status, String sort, Integer page, Integer pageSize) {
        Specification<Order> spec = OrderSpecification.buildSpecification(status);

        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isBlank()) {
            if (sort.startsWith("desc:")) {
                sortObj = Sort.by(Sort.Direction.DESC, sort.substring(5));
            } else if (sort.startsWith("asc:")) {
                sortObj = Sort.by(Sort.Direction.ASC, sort.substring(4));
            } else {
                sortObj = Sort.by(sort);
            }
        }
        
        int pageNumber = (page != null && page > 0) ? page - 1 : 0;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;

        Pageable pageable = PageRequest.of(pageNumber, size, sortObj);

        return orderRepository.findAll(spec, pageable);
    }

    public List<Order> getMyOrders(String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepository.findByUserId(user.getId());
    }
}