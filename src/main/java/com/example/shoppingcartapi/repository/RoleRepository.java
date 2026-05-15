package com.example.shoppingcartapi.repository;

import com.example.shoppingcartapi.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String roleUser);
}
