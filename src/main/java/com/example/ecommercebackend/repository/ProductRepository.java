package com.example.ecommercebackend.repository;

import com.example.ecommercebackend.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Product p SET p.inventory = p.inventory - :quantity WHERE p.id = :productId AND p.inventory >= :quantity")
    int decrementInventory(@Param("productId") UUID productId, @Param("quantity") int quantity);

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
