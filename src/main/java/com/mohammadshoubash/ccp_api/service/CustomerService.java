package com.mohammadshoubash.ccp_api.service;

import org.springframework.stereotype.Service;
import com.mohammadshoubash.ccp_api.repository.CustomerRepository;
import java.util.List;

import com.mohammadshoubash.ccp_api.dto.CustomerRequest;
import com.mohammadshoubash.ccp_api.entity.Customer;
import com.mohammadshoubash.ccp_api.exception.RecourceNotFoundException;

@Service 
public class CustomerService {
    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecourceNotFoundException("Customer not found with id " + id));
    }

    public Customer createCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        return repository.save(customer);
    }
    
    public Customer updateCustomer(Long id, CustomerRequest request) {
        Customer existingCustomer = repository.findById(id)
                .orElseThrow(() -> new RecourceNotFoundException("Customer not found with id " + id));
        existingCustomer.setName(request.name());
        existingCustomer.setEmail(request.email());
        existingCustomer.setPhone(request.phone());
        return repository.save(existingCustomer);
    }

    public void deleteCustomer(Long id) {
        repository.deleteById(id);
    }
}
