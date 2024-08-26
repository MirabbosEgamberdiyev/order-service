package fido.uz.Order.configuration;

import fido.uz.Order.entity.User;
import fido.uz.Order.entity.UserRole;
import fido.uz.Order.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner loadData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if the SUPER_ADMIN user exists
            if (userRepository.findByEmail("superadmin@example.com").isEmpty()) {
                User superAdmin = new User();
                superAdmin.setFullName("Super Admin");
                superAdmin.setEmail("superadmin@example.com");
                superAdmin.setPassword(passwordEncoder.encode("superadmin123")); // Default password
                superAdmin.setRoles(List.of(UserRole.ROLE_SUPER_ADMIN, UserRole.ROLE_ADMIN, UserRole.ROLE_USER));
                userRepository.save(superAdmin);
            }

            // Check if the ADMIN user exists
            if (userRepository.findByEmail("admin@example.com").isEmpty()) {
                User admin = new User();
                admin.setFullName("Admin User");
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("admin123")); // Default password
                admin.setRoles(List.of(UserRole.ROLE_ADMIN, UserRole.ROLE_USER));
                userRepository.save(admin);
            }

            // Check if the USER exists
            if (userRepository.findByEmail("user@example.com").isEmpty()) {
                User user = new User();
                user.setFullName("Regular User");
                user.setEmail("user@example.com");
                user.setPassword(passwordEncoder.encode("user123")); // Default password
                user.setRoles(List.of(UserRole.ROLE_USER));
                userRepository.save(user);
            }
        };
    }
}
