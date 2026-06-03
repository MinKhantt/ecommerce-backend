package com.example.shoppingcartapi.service.image;

import com.example.shoppingcartapi.dto.ImageDto;
import com.example.shoppingcartapi.entity.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IImageService {
    Image getImageById(UUID id);

    void deleteImageById(UUID id);

    List<ImageDto> saveImage(List<MultipartFile> files, UUID productId);

    void updateImage(MultipartFile file, UUID imageId);
}
