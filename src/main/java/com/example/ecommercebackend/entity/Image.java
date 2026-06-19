package com.example.ecommercebackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "images")
public class Image extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String fileName;
    private String fileType;
    private String downloadUrl;
    private String publicId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;
}
