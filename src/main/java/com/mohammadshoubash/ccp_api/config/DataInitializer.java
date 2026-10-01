package com.mohammadshoubash.ccp_api.config;

import com.mohammadshoubash.ccp_api.entity.AppUser;
import com.mohammadshoubash.ccp_api.entity.Role;
import com.mohammadshoubash.ccp_api.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository repository;
    private final PasswordEncoder encoder;

    public DataInitializer(AppUserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        create("admin", "admin123", Role.ADMIN);
        create("customer", "customer123", Role.CUSTOMER);
    }

    private void create(String username, String rawPassword, Role role) {
        if (!repository.existsByUsername(username)) {
            AppUser user = new AppUser(username, encoder.encode(rawPassword), role);
            repository.save(user);
        }
    }
}
