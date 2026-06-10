package com.example.shoppingcartapi.mapper;

import com.example.shoppingcartapi.dto.ImageDto;
import com.example.shoppingcartapi.entity.Image;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageMapper {

    private final ModelMapper modelMapper;

    public Image toImage(ImageDto imageDto) {
        return modelMapper.map(imageDto, Image.class);
    }

    public ImageDto toImageDto(Image image) {
        return modelMapper.map(image, ImageDto.class);
    }

}
