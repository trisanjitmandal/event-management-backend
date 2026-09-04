package com.aueventmanagement.config;


import com.aueventmanagement.entity.User;
import com.aueventmanagement.enums.Role;
import com.aueventmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin.email:}")
    private String adminEmail;

    @Value("${app.seed.admin.password:}")
    private String adminPassword;

    @Value("${app.seed.admin.name:}")
    private String adminName;

    @Override
    public void run(String... args) {

        // Create default admin only if no ADMIN exists
        if (!userRepository.existsByRole(Role.ADMIN)) {

            User admin = new User();

            admin.setName(adminName);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);

            System.out.println(" Default Admin Created Successfully");
            System.out.println(" Email    : admin@event.com");
            System.out.println(" Password : Admin@123");
        }
    }
}
