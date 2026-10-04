package com.mohammadshoubash.ccp_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.mohammadshoubash.ccp_api.repository.AppUserRepository;
import java.util.List;
import java.util.stream.Collectors;

import com.mohammadshoubash.ccp_api.dto.CustomerRequest;
import com.mohammadshoubash.ccp_api.dto.CustomerResponse;
import com.mohammadshoubash.ccp_api.entity.AppUser;
import com.mohammadshoubash.ccp_api.entity.Role;
import com.mohammadshoubash.ccp_api.exception.DuplicateResourceException;
import com.mohammadshoubash.ccp_api.exception.ResourceNotFoundException;

@Service 
public class CustomerService {
    private final AppUserRepository repository;
    private final PasswordEncoder encoder;

    public CustomerService(AppUserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public List<CustomerResponse> getAllCustomers() {
        return repository.findByRole(Role.CUSTOMER).stream().map(CustomerResponse::new).collect(Collectors.toList());
    }

    public CustomerResponse getCustomerById(Long id) {
        AppUser user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));
        return new CustomerResponse(user);
    }

    public AppUser createCustomer(CustomerRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email is already registered");
        }
        
        if (repository.existsByPhone(request.phone())) {
            throw new DuplicateResourceException("Phone number is already registered");
        }

        if (repository.existsByUsername(request.email())) {
            throw new DuplicateResourceException("Username (email) is already registered");
        }
        
        AppUser user = new AppUser();

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setRole(Role.CUSTOMER);
        user.setUsername(request.email());
        user.setPassword(encoder.encode(request.phone()));
        
        return repository.save(user);
    }
    
    public AppUser updateCustomer(Long id, CustomerRequest request) {
        AppUser existingCustomer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));
        
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
            throw new ResourceNotFoundException("Customer not found with id " + id);
        }
    }
}
