package com.example.shoppingcartapi.dto.response;

import com.example.shoppingcartapi.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse implements Serializable {
    private List<ProductDto> products;
}
