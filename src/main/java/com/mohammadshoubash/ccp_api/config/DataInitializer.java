package com.mohammadshoubash.ccp_api.config;

import com.mohammadshoubash.ccp_api.entity.AppUser;
import com.mohammadshoubash.ccp_api.entity.Customer;
import com.mohammadshoubash.ccp_api.entity.Role;
import com.mohammadshoubash.ccp_api.repository.AppUserRepository;
import com.mohammadshoubash.ccp_api.repository.CustomerRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final AppUserRepository userRepository;
    private final PasswordEncoder encoder;

    public DataInitializer(CustomerRepository customerRepository, AppUserRepository userRepository, PasswordEncoder encoder) {
        this.customerRepository = customerRepository;
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // Admin user
        if(!userRepository.existsByUsername("admin")){
            AppUser admin = new AppUser("admin", encoder.encode("admin123"), Role.ADMIN);
            userRepository.save(admin);
        }

        // Customer account with user
        if(!userRepository.existsByUsername("customer")){
            AppUser user = new AppUser("customer", encoder.encode("customer123"), Role.CUSTOMER);
            user = userRepository.save(user);
            
            Customer customer = new Customer();
            customer.setName("Demo Customer");
            customer.setEmail("customer@example.com");
            customer.setPhone("1234567890");
            customer.setUser(user);
            customerRepository.save(customer);
        }
    }
}