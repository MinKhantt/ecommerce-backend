package com.example.ecommercebackend.service.auth;

import com.example.ecommercebackend.dto.request.LoginRequest;
import com.example.ecommercebackend.dto.response.JwtResponse;
import com.example.ecommercebackend.dto.response.TokenResponse;

public interface IAuthService {
    TokenResponse login(LoginRequest request);
    TokenResponse refresh(String refreshToken);
    void logout(String refreshToken);
    JwtResponse exchangeOAuth2Code(String code);
}
