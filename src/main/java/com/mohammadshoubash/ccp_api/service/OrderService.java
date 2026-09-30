package com.mohammadshoubash.ccp_api.service;

import com.mohammadshoubash.ccp_api.dto.OrderRequest;
import com.mohammadshoubash.ccp_api.entity.Order;
import com.mohammadshoubash.ccp_api.entity.OrderStatus;
import com.mohammadshoubash.ccp_api.exception.ResourceNotFoundException;
import com.mohammadshoubash.ccp_api.repository.OrderRepository;
import com.mohammadshoubash.ccp_api.repository.CustomerRepository;
import com.mohammadshoubash.ccp_api.entity.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public Order createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        
        if (orderRequest == null) {
            throw new IllegalArgumentException("Order and customer cannot be null");
        }

        // Default values if not provided
        order.setStatus(OrderStatus.valueOf(orderRequest.status().toUpperCase()));
        
        Customer customer = customerRepository.findById(orderRequest.customer_id())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + orderRequest.customer_id()));

        order.setCustomer(customer);
        order.setTotal(orderRequest.total());

        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            return order.get();
        } else {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public Order updateOrderStatus(Long id, OrderStatus status) {
        Optional<Order> orderOpt = orderRepository.findById(id);

        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            return orderRepository.save(order);
        } else {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }
}
