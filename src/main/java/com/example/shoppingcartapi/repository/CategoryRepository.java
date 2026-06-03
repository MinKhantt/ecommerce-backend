package com.example.shoppingcartapi.repository;

import com.example.shoppingcartapi.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Category findByName(String name);

    boolean existsByName(String name);
}
