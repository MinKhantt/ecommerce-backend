package com.example.ecommercebackend.repository;

import com.example.ecommercebackend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Role findByName(String roleUser);
}
