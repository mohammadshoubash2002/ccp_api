package com.mohammadshoubash.ccp_api.service;

import com.mohammadshoubash.ccp_api.dto.OrderRequest;
import com.mohammadshoubash.ccp_api.entity.Order;
import com.mohammadshoubash.ccp_api.entity.OrderStatus;
import com.mohammadshoubash.ccp_api.exception.ResourceNotFoundException;
import com.mohammadshoubash.ccp_api.repository.OrderRepository;
import com.mohammadshoubash.ccp_api.repository.CustomerRepository;
import com.mohammadshoubash.ccp_api.entity.Customer;
import com.mohammadshoubash.ccp_api.specification.OrderSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isPresent()) {
            return orderRepository.findByCustomerId(customer.get().getId());
        } else {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
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
}