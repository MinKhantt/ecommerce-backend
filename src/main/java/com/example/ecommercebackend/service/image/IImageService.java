package com.example.ecommercebackend.service.image;

import com.example.ecommercebackend.dto.ImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IImageService {

    List<ImageDto> uploadImage(List<MultipartFile> files, UUID productId);

    ImageDto getImageById(UUID id);

    void updateImage(MultipartFile file, UUID imageId);

    void deleteImageById(UUID id);
}
