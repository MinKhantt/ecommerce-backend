package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.ImageDto;
import com.example.ecommercebackend.dto.response.ApiResponse;
import com.example.ecommercebackend.exception.ResourceNotFoundException;
import com.example.ecommercebackend.service.image.IImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/images")
public class ImageController {
    private final IImageService imageService;
    private final WebClient webClient;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Upload images", description = "Upload one or more images for a product, admin only")
    public ResponseEntity<ApiResponse> saveImage(
            @RequestParam List<MultipartFile> file,
            @RequestParam UUID productId
    ) {
        List<ImageDto> imageDto = imageService.uploadImage(file, productId);
        return ResponseEntity.ok(new ApiResponse("Upload success!", imageDto));
    }

    @GetMapping("/view/{imageId}")
    @Transactional(readOnly = true)
    @Operation(summary = "View image", description = "Redirect to Cloudinary image URL")
    public RedirectView viewImage(@PathVariable UUID imageId) {
        ImageDto imageDto = imageService.getImageById(imageId);
        return new RedirectView(imageDto.getDownloadUrl(), true);
    }

    @GetMapping("/download/{imageId}")
    @Transactional(readOnly = true)
    @Operation(summary = "Download image", description = "Proxy download image file from Cloudinary")
    public ResponseEntity<Resource> downloadImage(@PathVariable UUID imageId) throws SQLException {
        ImageDto imageDto = imageService.getImageById(imageId);

        byte[] imageBytes = webClient.get()
                .uri(imageDto.getDownloadUrl())
                .retrieve()
                .bodyToMono(byte[].class)
                .block();

        if (imageBytes == null) {
            throw new ResourceNotFoundException("Image not found");
        }
        ByteArrayResource resource = new ByteArrayResource(imageBytes);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageDto.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +  imageDto.getFileName() + "\"")
                .body(resource);
    }

    @PutMapping("/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update image", description = "Replace an existing image file, admin only")
    public ResponseEntity<ApiResponse> updateImage(
            @PathVariable UUID imageId,
            @RequestParam MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file provided");
        }

        imageService.getImageById(imageId);
        imageService.updateImage(file, imageId);
        return ResponseEntity.ok(new ApiResponse("Update Success!", null));
    }

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete image", description = "Delete an image by ID, admin only")
    public ResponseEntity<ApiResponse> deleteImage(
            @PathVariable UUID imageId
    ) {
        imageService.getImageById(imageId);
        imageService.deleteImageById(imageId);
        return ResponseEntity.ok(new ApiResponse("Delete Success!", null));
    }
}
