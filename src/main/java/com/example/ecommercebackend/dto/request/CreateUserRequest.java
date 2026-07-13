package com.example.ecommercebackend.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @NotBlank(message = "Email name is required")
    private String email;
    @NotBlank(message = "Password name is required")
    private String password;
}
