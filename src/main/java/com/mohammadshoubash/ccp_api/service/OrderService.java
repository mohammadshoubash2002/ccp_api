package com.mohammadshoubash.ccp_api.service;

import com.mohammadshoubash.ccp_api.dto.OrderRequest;
import com.mohammadshoubash.ccp_api.dto.OrderResponse;
import com.mohammadshoubash.ccp_api.entity.Order;
import com.mohammadshoubash.ccp_api.entity.OrderStatus;
import com.mohammadshoubash.ccp_api.entity.Role;
import com.mohammadshoubash.ccp_api.exception.ResourceNotFoundException;
import com.mohammadshoubash.ccp_api.repository.OrderRepository;
import com.mohammadshoubash.ccp_api.repository.AppUserRepository;
import com.mohammadshoubash.ccp_api.repository.CustomerRepository;
import com.mohammadshoubash.ccp_api.entity.AppUser;
import com.mohammadshoubash.ccp_api.entity.Customer;
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
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = new Order();

        if (orderRequest == null) {
            throw new IllegalArgumentException("Order and customer cannot be null");
        }

        order.setStatus(OrderStatus.valueOf(orderRequest.status().toUpperCase()));
        
        Customer customer = customerRepository.findById(orderRequest.customer_id())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + orderRequest.customer_id()));

        order.setCustomer(customer);
        order.setTotal(orderRequest.total());

        order = orderRepository.save(order);

        return new OrderResponse(
            order.getId(),
            order.getCustomer().getId(),
            order.getTotal(),
            order.getStatus()
        );
    }

    public OrderResponse getOrderById(Long id, String username) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        AppUser currentUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        // Admin can view any order
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        
        // Customer can only view their own order
        boolean isOwner = order.getCustomer() != null 
                && order.getCustomer().getUser() != null 
                && order.getCustomer().getUser().getUsername().equals(username);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You don't have permission to view this order");
        }

        return new OrderResponse(order);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(OrderResponse::new).toList();
    }

    @PreAuthorize("hasRole('ADMIN') or #customer_id == authentication.principal.id")
    public List<OrderResponse> getOrdersByCustomerId(Long customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isPresent()) {
            return orderRepository.findByCustomerId(customer.get().getId()).stream().map(OrderResponse::new).toList();
        } else {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
    }

    public OrderResponse updateOrderStatus(Long id, OrderStatus status, String username) {
        Optional<Order> orderOpt = orderRepository.findById(id);

        AppUser currentUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        // Admin can view any order
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        
        // Customer can only view their own order
        boolean isOwner = orderOpt.get().getCustomer() != null 
                && orderOpt.get().getCustomer().getUser() != null 
                && orderOpt.get().getCustomer().getUser().getUsername().equals(username);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You don't have permission to update this order");
        }

        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            return new OrderResponse(orderRepository.save(order));
        } else {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
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
        if (user.getCustomer() == null) {
            throw new ResourceNotFoundException("No customer profile found for this user");
        }
        return orderRepository.findByCustomerId(user.getCustomer().getId());
    }
}