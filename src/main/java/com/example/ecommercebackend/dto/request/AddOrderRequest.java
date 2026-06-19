package com.example.ecommercebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddOrderRequest {

    @NotBlank(message = "Shipping Address Required")
    private String shippingAddress;
}
