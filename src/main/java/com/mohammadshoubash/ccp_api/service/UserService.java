package com.mohammadshoubash.ccp_api.service;

import com.mohammadshoubash.ccp_api.dto.RegisterRequest;
import com.mohammadshoubash.ccp_api.dto.UserResponse;
import com.mohammadshoubash.ccp_api.entity.AppUser;
import com.mohammadshoubash.ccp_api.entity.Customer;
import com.mohammadshoubash.ccp_api.entity.Role;
import com.mohammadshoubash.ccp_api.exception.DuplicateResourceException;
import com.mohammadshoubash.ccp_api.repository.AppUserRepository;
import com.mohammadshoubash.ccp_api.repository.CustomerRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mohammadshoubash.ccp_api.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class UserService {

    private final AppUserRepository repository;
    private final PasswordEncoder encoder;
    private final CustomerRepository customerRepository;

    public UserService(AppUserRepository repository, PasswordEncoder encoder, CustomerRepository customerRepository) {
        this.repository = repository;
        this.encoder = encoder;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (repository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username already taken");
        }
        AppUser user = new AppUser(
                request.username(),
                encoder.encode(request.password()),
                Role.CUSTOMER);

        user = repository.save(user);

        // Automatically create a customer profile linked to this user
        Customer customer = new Customer();

        customer.setName(request.username());
        customer.setEmail(request.username() + "@example.com");
        customer.setPhone("N/A");
        customer.setUser(user);
        customerRepository.save(customer);

        return UserResponse.from(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> findAll() {
        return repository.findAll().stream().map(UserResponse::from).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponse changeRole(Long id, Role role) {
        AppUser user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        user.setRole(role);

        return UserResponse.from(user);
    }
}
