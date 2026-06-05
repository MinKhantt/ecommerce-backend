package com.example.shoppingcartapi.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ImageDto {
    private UUID id;
    private String fileName;
    private String fileType;
    private String downloadUrl;
    private String publicId;
}
