package com.example.shoppingcartapi.repository;

import com.example.shoppingcartapi.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
    List<Image> findByProductId(UUID id);
}
