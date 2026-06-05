package com.example.shoppingcartapi.data;

import com.example.shoppingcartapi.entity.Role;
import com.example.shoppingcartapi.entity.User;
import com.example.shoppingcartapi.repository.RoleRepository;
import com.example.shoppingcartapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@RequiredArgsConstructor
@Component
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Set<String> defaultRoles = Set.of("ROLE_ADMIN", "ROLE_USER");
        createDefaultRoleIfNotExist(defaultRoles);
        createDefaultAdminIfNotExist();
    }

    private void createDefaultRoleIfNotExist(Set<String> roles) {
        roles.forEach(roleName -> {
            Role existingRole = roleRepository.findByName(roleName);
            if (existingRole == null) {
                Role newRole = new Role(roleName);
                roleRepository.save(newRole);
                System.out.println("Default role " + roleName + " created successfully");
            }
        });
    }

    private void createDefaultAdminIfNotExist() {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");

        if (!userRepository.existsByEmail(adminEmail)) {
            User user = new User();
            user.setFirstName("Admin");
            user.setLastName("2");
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setEmail(adminEmail);
            user.setRoles(Set.of(adminRole));
            userRepository.save(user);
            System.out.println("Default admin user created successfully");
        }
    }
}
