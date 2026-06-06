package com.example.shoppingcartapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddOrderRequest {

    @NotBlank(message = "Shipping Address Required")
    private String shippingAddress;
}
