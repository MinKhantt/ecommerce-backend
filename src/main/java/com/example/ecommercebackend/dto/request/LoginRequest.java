package com.example.ecommercebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email name is required")
    private String email;

    @NotBlank(message = "Password name is required")
    private String password;
}
