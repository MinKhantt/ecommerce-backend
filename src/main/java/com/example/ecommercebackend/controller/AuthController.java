package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.UserDto;
import com.example.ecommercebackend.dto.request.CreateUserRequest;
import com.example.ecommercebackend.dto.request.LoginRequest;
import com.example.ecommercebackend.dto.response.ApiResponse;
import com.example.ecommercebackend.dto.response.JwtResponse;
import com.example.ecommercebackend.security.jwt.JwtUtils;
import com.example.ecommercebackend.security.user.ShopUserDetails;
import com.example.ecommercebackend.security.user.ShopUserDetailsService;
import com.example.ecommercebackend.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("${api.prefix}/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final ShopUserDetailsService shopUserDetailsService;
    private final StringRedisTemplate redisTemplate;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody CreateUserRequest request) {
        UserDto userDto = userService.createUser(request);
        return ResponseEntity.ok(new ApiResponse("Registration Successful", userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken  = jwtUtils.generateAccessToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        String tokenId = jwtUtils.extractTokenId(refreshToken);
        redisTemplate.opsForValue().set(
                "refresh:" + tokenId,
                refreshToken,
                30,
                TimeUnit.DAYS
        );

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/api/v1/auth")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        ShopUserDetails userDetails = (ShopUserDetails) authentication.getPrincipal();
        JwtResponse jwtResponse = new JwtResponse(userDetails.getId(), accessToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse("Login Successful", jwtResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {

        if (refreshToken == null || !jwtUtils.isTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse("Refresh Token Not Valid or Expired", null));
        }

        String oldTokenId = jwtUtils.extractTokenId(refreshToken);

        if (!redisTemplate.hasKey("refresh:" + oldTokenId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse("Refresh token has been revoked, please login again", null));
        }

        String email = jwtUtils.extractUsername(refreshToken);

        ShopUserDetails userDetails = (ShopUserDetails) shopUserDetailsService.loadUserByUsername(email);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String newAccessToken = jwtUtils.generateAccessToken(authentication);
        String newRefreshToken = jwtUtils.generateRefreshToken(authentication);

        redisTemplate.delete("refresh:" + oldTokenId);

        String newTokenId = jwtUtils.extractTokenId(newRefreshToken);
        redisTemplate.opsForValue().set(
                "refresh:" + newTokenId,
                newRefreshToken,
                30,
                TimeUnit.DAYS
        );

        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/api/v1/auth")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        JwtResponse jwtResponse = new JwtResponse(userDetails.getId(), newAccessToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse("Refresh Successful", jwtResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken != null && jwtUtils.isTokenValid(refreshToken)) {
            String tokenId = jwtUtils.extractTokenId(refreshToken);
            redisTemplate.delete("refresh:" + tokenId);
        }

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/api/v1/auth")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse("Logged out successfully", null));
    }
}
