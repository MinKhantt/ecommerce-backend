package com.example.shoppingcartapi.controller;

import com.example.shoppingcartapi.dto.ImageDto;
import com.example.shoppingcartapi.dto.response.ApiResponse;
import com.example.shoppingcartapi.exception.ResourceNotFoundException;
import com.example.shoppingcartapi.entity.Image;
import com.example.shoppingcartapi.mapper.ImageMapper;
import com.example.shoppingcartapi.service.image.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/images")
public class ImageController {
    private final IImageService imageService;
    private final WebClient webClient;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> saveImage(
            @RequestParam List<MultipartFile> file,
            @RequestParam UUID productId
    ) {
        try {
            List<ImageDto> imageDto = imageService.uploadImage(file, productId);
            return ResponseEntity.ok(new ApiResponse("Upload success!", imageDto));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Upload failed!", e.getMessage()));
        }
    }

    @GetMapping("/view/{imageId}")
    @Transactional(readOnly = true)
    public RedirectView viewImage(@PathVariable UUID imageId) {
        ImageDto imageDto = imageService.getImageById(imageId);
        return new RedirectView(imageDto.getDownloadUrl(), true);
    }

    @GetMapping("/download/{imageId}")
    @Transactional(readOnly = true)
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
    public ResponseEntity<ApiResponse> updateImage(
            @PathVariable UUID imageId,
            @RequestParam MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("No file provided!", null));
        }

        try {
            ImageDto imageDto = imageService.getImageById(imageId);

            if (imageDto != null) {
                imageService.updateImage(file, imageId);
                return ResponseEntity.ok(new ApiResponse("Update Success!", null));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("Delete failed!", INTERNAL_SERVER_ERROR));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<ApiResponse> deleteImage(
            @PathVariable UUID imageId
    ) {
        try {
            ImageDto imageDto = imageService.getImageById(imageId);

            if (imageDto != null) {
                imageService.deleteImageById(imageId);
                return ResponseEntity.ok(new ApiResponse("Delete Success!", null));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("Delete failed!", INTERNAL_SERVER_ERROR));
    }
}
