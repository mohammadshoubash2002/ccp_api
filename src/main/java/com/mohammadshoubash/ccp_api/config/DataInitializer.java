package com.mohammadshoubash.ccp_api.config;

import com.mohammadshoubash.ccp_api.entity.AppUser;
import com.mohammadshoubash.ccp_api.entity.Role;
import com.mohammadshoubash.ccp_api.repository.AppUserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final PasswordEncoder encoder;

    public DataInitializer(AppUserRepository userRepository, PasswordEncoder encoder) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // Admin user
        if (!userRepository.existsByUsername("admin")) {
            AppUser admin = new AppUser("admin", encoder.encode("admin123"), "Admin User", "admin@ccp.com", "0000000000", Role.ADMIN);
            userRepository.save(admin);
        }

        // Customer account with user
        if (!userRepository.existsByUsername("customer")) {
            AppUser user = new AppUser("customer", encoder.encode("customer123"), "Customer", "customer@ccp.com", "1111111111", Role.CUSTOMER);
            userRepository.save(user);
        }
    }
}