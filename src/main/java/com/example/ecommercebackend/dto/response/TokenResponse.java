package com.example.ecommercebackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {
    private JwtResponse jwtResponse;
    private String refreshToken;
}
