package com.mohammadshoubash.ccp_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import com.mohammadshoubash.ccp_api.dto.CustomerRequest;
import com.mohammadshoubash.ccp_api.dto.MessageResponse;
import com.mohammadshoubash.ccp_api.entity.Customer;
import com.mohammadshoubash.ccp_api.entity.Order;
import com.mohammadshoubash.ccp_api.entity.Ticket;
import com.mohammadshoubash.ccp_api.service.CustomerService;
import com.mohammadshoubash.ccp_api.service.OrderService;
import com.mohammadshoubash.ccp_api.service.TicketService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired 
    private OrderService orderService;

    @Autowired
    private TicketService ticketService;
    
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }
    
    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }
    
    @PostMapping
    public Customer createCustomer(@Valid @RequestBody CustomerRequest request) {
        return customerService.createCustomer(request);
    }
    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return customerService.updateCustomer(id, request);
    }
    
    @DeleteMapping("/{id}")
    public MessageResponse deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return new MessageResponse("Customer deleted successfully");
    }

    @GetMapping("/{customerId}/orders")
    public List<Order> getCustomerOrders(@PathVariable Long customerId) {
        return orderService.getOrdersByCustomerId(customerId);
    }

    @GetMapping("/{customerId}/tickets")
    public List<Ticket> getCustomerTickets(@PathVariable Long customerId) {
        return ticketService.getTicketsByCustomerId(customerId);
    }
}