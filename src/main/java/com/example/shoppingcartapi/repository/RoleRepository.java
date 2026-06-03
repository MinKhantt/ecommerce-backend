package com.example.shoppingcartapi.repository;

import com.example.shoppingcartapi.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Role findByName(String roleUser);
}
