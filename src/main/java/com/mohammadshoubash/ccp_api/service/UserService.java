package com.mohammadshoubash.ccp_api.service;

import com.mohammadshoubash.ccp_api.dto.RegisterRequest;
import com.mohammadshoubash.ccp_api.dto.UserResponse;
import com.mohammadshoubash.ccp_api.entity.AppUser;
import com.mohammadshoubash.ccp_api.entity.Role;
import com.mohammadshoubash.ccp_api.exception.DuplicateResourceException;
import com.mohammadshoubash.ccp_api.exception.ResourceNotFoundException;
import com.mohammadshoubash.ccp_api.repository.AppUserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final AppUserRepository repository;
    private final PasswordEncoder encoder;

    public UserService(AppUserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (repository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username already taken");
        }

        if (request.email() != null && !request.email().isBlank() && repository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email is already registered");
        }

        if (request.phone() != null && !request.phone().isBlank() && repository.existsByPhone(request.phone())) {
            throw new DuplicateResourceException("Phone number is already registered");
        }

        String name = (request.name() != null && !request.name().isBlank()) ? request.name() : null;
        String email = (request.email() != null && !request.email().isBlank()) ? request.email() : null;
        String phone = (request.phone() != null && !request.phone().isBlank()) ? request.phone() : null;

        AppUser user = new AppUser(
                request.username(),
                encoder.encode(request.password()),
                name,
                email,
                phone,
                Role.CUSTOMER);

        user = repository.save(user);

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
