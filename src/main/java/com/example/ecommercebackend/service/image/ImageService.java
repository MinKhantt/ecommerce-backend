package com.example.ecommercebackend.service.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.ecommercebackend.dto.ImageDto;
import com.example.ecommercebackend.dto.ProductDto;
import com.example.ecommercebackend.entity.Product;
import com.example.ecommercebackend.exception.ResourceNotFoundException;
import com.example.ecommercebackend.entity.Image;
import com.example.ecommercebackend.mapper.ImageMapper;
import com.example.ecommercebackend.mapper.ProductMapper;
import com.example.ecommercebackend.repository.ImageRepository;
import com.example.ecommercebackend.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService{
    private final ImageRepository imageRepository;
    private final IProductService productService;
    private final ProductMapper productMapper;
    private final ImageMapper imageMapper;
    private final Cloudinary cloudinary;

    @Override
    @Transactional
    public List<ImageDto> uploadImage(List<MultipartFile> files, UUID productId) {
        ProductDto productDto = productService.getProductById(productId);
        Product product = productMapper.ProductDtoToProduct(productDto);

        List<ImageDto> savedImagesDto = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("secure_url");
                String publicId = (String) uploadResult.get("public_id");

                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setDownloadUrl(imageUrl);
                image.setProduct(product);
                image.setPublicId(publicId);

                Image savedImage = imageRepository.save(image);

                ImageDto imageDto = imageMapper.toImageDto(savedImage);
                savedImagesDto.add(imageDto);

            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }
        return savedImagesDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ImageDto getImageById(UUID id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No image found with id: " + id));
        return imageMapper.toImageDto(image);
    }

    @Override
    public void updateImage(MultipartFile file, UUID imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("No image found with id: " + imageId));

        Product currentProduct = image.getProduct();

        try {
            if (image.getPublicId() != null) {
                cloudinary.uploader().destroy(image.getPublicId(), ObjectUtils.emptyMap());
            }

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String newUrl = (String) uploadResult.get("secure_url");
            String newPublicId = (String) uploadResult.get("public_id");

            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setDownloadUrl(newUrl);
            image.setPublicId(newPublicId);
            image.setProduct(currentProduct);

            imageRepository.save(image);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update image: " + e.getMessage());
        }
    }

    @Override
    public void deleteImageById(UUID imageId) {
        ImageDto imageDto = getImageById(imageId);
        Image image = imageMapper.toImage(imageDto);

        try {
            if (image.getPublicId() != null) {
                cloudinary.uploader().destroy(image.getPublicId(), ObjectUtils.emptyMap());
                imageRepository.delete(image);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image: " + e.getMessage());
        }
    }
}
