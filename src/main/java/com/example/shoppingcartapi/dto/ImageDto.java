package com.example.shoppingcartapi.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ImageDto {
    private UUID imageId;
    private String fileName;
    private String downloadUrl;
}
