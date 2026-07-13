package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.UserDto;
import com.example.ecommercebackend.dto.request.CreateUserRequest;
import com.example.ecommercebackend.dto.request.LoginRequest;
import com.example.ecommercebackend.dto.response.ApiResponse;
import com.example.ecommercebackend.dto.response.TokenResponse;
import com.example.ecommercebackend.service.auth.IAuthService;
import com.example.ecommercebackend.service.user.IUserService;
import com.example.ecommercebackend.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("${api.prefix}/auth")
public class AuthController {

    private final IUserService userService;
    private final IAuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Create a new user account")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody CreateUserRequest request) {
        UserDto userDto = userService.createUser(request);
        return ResponseEntity.ok(new ApiResponse("Registration Successful", userDto));
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate and return JWT access token with refresh cookie")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        ResponseCookie cookie = CookieUtil.createRefreshCookie(tokenResponse.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse("Login Successful", tokenResponse.getJwtResponse()));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Rotate refresh token and return new access token")
    public ResponseEntity<ApiResponse> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse("Refresh Token Not Valid or Expired", null));
        }

        TokenResponse tokenResponse = authService.refresh(refreshToken);
        ResponseCookie cookie = CookieUtil.createRefreshCookie(tokenResponse.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse("Refresh Successful", tokenResponse.getJwtResponse()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Revoke refresh token and clear auth cookie")
    public ResponseEntity<ApiResponse> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        authService.logout(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, CookieUtil.clearRefreshCookie().toString())
                .body(new ApiResponse("Logged out successfully", null));
    }
}
