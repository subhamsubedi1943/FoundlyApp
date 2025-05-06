
package com.foundly.app2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import com.foundly.app2.entity.User;
import com.foundly.app2.repository.UserRepository;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class FoundlyApplication {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public FoundlyApplication(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(FoundlyApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDefaultAdminUser() {
        return args -> {
            List<User> admins = userRepository.findAll().stream()
                    .filter(user -> user.getRole() == User.Role.ADMIN)
                    .toList();

            if (admins.isEmpty()) {
                User defaultAdmin = new User();
                defaultAdmin.setEmployeeId("001");
                defaultAdmin.setName("admin");
                defaultAdmin.setEmail("admin@001");
                defaultAdmin.setUsername("admin@admin");
                defaultAdmin.setPassword(passwordEncoder.encode("admin"));
                defaultAdmin.setRole(User.Role.ADMIN);
                defaultAdmin.setSecurity(false);

                userRepository.save(defaultAdmin);
                System.out.println("Default admin user created.");
            } else {
                System.out.println("Admin user(s) already exist.");
            }
        };
    }
}
