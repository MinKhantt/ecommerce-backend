package com.example.ecommercebackend.mapper;


import com.example.ecommercebackend.dto.ProductDto;
import com.example.ecommercebackend.dto.request.AddProductRequest;
import com.example.ecommercebackend.dto.request.ProductUpdateRequest;
import com.example.ecommercebackend.entity.Product;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductMapper {

    private final ModelMapper modelMapper;

    public Product toProduct(AddProductRequest request) {
        return modelMapper.map(request, Product.class);
    }

    public void toUpdateProduct(ProductUpdateRequest request, Product existingProduct) {
        modelMapper.map(request, existingProduct);
    }

    public Product ProductDtoToProduct(ProductDto productDto) {
        return modelMapper.map(productDto, Product.class);
    }

    public ProductDto ProductToProductDto(Product product) {
        return modelMapper.map(product, ProductDto.class);
    }
}
