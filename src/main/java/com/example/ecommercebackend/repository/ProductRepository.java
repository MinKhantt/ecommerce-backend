package com.example.ecommercebackend.repository;

import com.example.ecommercebackend.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @EntityGraph(attributePaths = {"images", "category"})
    List<Product> findByCategoryName(String category);

    @EntityGraph(attributePaths = {"images", "category"})
    List<Product> findByBrand(String brand);

    @EntityGraph(attributePaths = {"images", "category"})
    List<Product> findByCategoryNameAndBrand(String category, String brand);

    @EntityGraph(attributePaths = {"images", "category"})
    List<Product> findByName(String name);

    @EntityGraph(attributePaths = {"images", "category"})
    List<Product> findByBrandAndName(String brand, String name);

    Long countByBrandAndName(String brand, String name);

    boolean existsByNameAndBrand(String name, String brand);
}
