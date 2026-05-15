package com.example.shoppingcartapi.service.image;

import com.example.shoppingcartapi.dto.ImageDto;
import com.example.shoppingcartapi.entity.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {
    Image getImageById(Long id);

    void deleteImageById(Long id);

    List<ImageDto> saveImage(List<MultipartFile> files, Long productId);

    void updateImage(MultipartFile file, Long imageId);
}
