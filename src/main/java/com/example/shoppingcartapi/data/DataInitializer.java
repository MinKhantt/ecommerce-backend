package com.example.shoppingcartapi.data;

import com.example.shoppingcartapi.entity.Role;
import com.example.shoppingcartapi.entity.User;
import com.example.shoppingcartapi.repository.RoleRepository;
import com.example.shoppingcartapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Transactional
@RequiredArgsConstructor
@Component
public class DataInitializer implements ApplicationListener {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        Set<String> defaultRole = Set.of("ROLE_ADMIN", "ROLE_USER");
        createDefaultUserIfNotExist();
        createDefaultRoleIfNotExist(defaultRole);
        createDefaultAdminIfNotExist();
    }

    private void createDefaultUserIfNotExist() {
        Role userRole = roleRepository.findByName("ROLE_USER");
        for (int i = 1; i <= 5; i++) {
            String defaultEmail = "user"+i+"@email.com";
            if (userRepository.existsByEmail(defaultEmail)) {
                continue;
            }
            User user = new User();
            user.setFirstName("The User");
            user.setLastName("User" + i);
            user.setPassword(passwordEncoder.encode("12345"));
            user.setEmail(defaultEmail);
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
            System.out.println("Default user " + i + " created successfully");
        }
    }

    private void createDefaultAdminIfNotExist() {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        for (int i = 1; i <= 5; i++) {
            String defaultEmail = "admin"+i+"@email.com";
            if (userRepository.existsByEmail(defaultEmail)) {
                continue;
            }
            User user = new User();
            user.setFirstName("Admin");
            user.setLastName("Admin" + i);
            user.setPassword(passwordEncoder.encode("12345"));
            user.setEmail(defaultEmail);
            user.setRoles(Set.of(adminRole));
            userRepository.save(user);
            System.out.println("Default admin user " + i + " created successfully");

        }
    }

    private void createDefaultRoleIfNotExist(Set<String> roles) {
        roles.forEach(roleName -> {
            // Check if the role already exists in the DB
            Role existingRole = roleRepository.findByName(roleName);

            // If it doesn't exist, create and save it
            if (existingRole == null) {
                Role newRole = new Role(roleName);
                roleRepository.save(newRole);
                System.out.println("Default role " + roleName + " created successfully");
            }
        });
    }
}
