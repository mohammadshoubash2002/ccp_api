package com.mohammadshoubash.ccp_api.service;

import org.springframework.stereotype.Service;
import com.mohammadshoubash.ccp_api.repository.CustomerRepository;
import java.util.List;

import com.mohammadshoubash.ccp_api.dto.CustomerRequest;
import com.mohammadshoubash.ccp_api.dto.MessageResponse;
import com.mohammadshoubash.ccp_api.entity.Customer;
import com.mohammadshoubash.ccp_api.exception.DuplicateResourceException;
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
        if (repository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email is already registered");
        }
        
        if (repository.existsByPhone(request.phone())) {
            throw new DuplicateResourceException("Phone number is already registered");
        }

        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        return repository.save(customer);
    }
    
    public Customer updateCustomer(Long id, CustomerRequest request) {
        Customer existingCustomer = repository.findById(id)
                .orElseThrow(() -> new RecourceNotFoundException("Customer not found with id " + id));
        
        if (repository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateResourceException("Email is already registered by another customer");
        }
        
        if (repository.existsByPhoneAndIdNot(request.phone(), id)) {
            throw new DuplicateResourceException("Phone number is already registered by another customer");
        }
        
        existingCustomer.setName(request.name());
        existingCustomer.setEmail(request.email());
        existingCustomer.setPhone(request.phone());
        
        return repository.save(existingCustomer);
    }

    public void deleteCustomer(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RecourceNotFoundException("Customer not found with id " + id);
        }
    }
}
