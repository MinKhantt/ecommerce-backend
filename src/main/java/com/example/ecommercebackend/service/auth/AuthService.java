package com.example.ecommercebackend.service.auth;

import com.example.ecommercebackend.dto.request.LoginRequest;
import com.example.ecommercebackend.dto.response.JwtResponse;
import com.example.ecommercebackend.dto.response.TokenResponse;
import com.example.ecommercebackend.security.jwt.JwtUtils;
import com.example.ecommercebackend.security.user.ShopUserDetails;
import com.example.ecommercebackend.security.user.ShopUserDetailsService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final StringRedisTemplate redisTemplate;
    private final ShopUserDetailsService shopUserDetailsService;

    @Override
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.generateAccessToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        String tokenId = jwtUtils.extractTokenId(refreshToken);
        redisTemplate.opsForValue().set("refresh:" + tokenId, refreshToken, 30, TimeUnit.DAYS);

        ShopUserDetails userDetails = (ShopUserDetails) authentication.getPrincipal();
        JwtResponse jwtResponse = new JwtResponse(userDetails.getId(), accessToken);

        return new TokenResponse(jwtResponse, refreshToken);
    }

    @Override
    public TokenResponse refresh(String refreshToken) {
        if (!jwtUtils.isTokenValid(refreshToken)) {
            throw new JwtException("Refresh Token Not Valid or Expired");
        }

        String oldTokenId = jwtUtils.extractTokenId(refreshToken);

        if (!redisTemplate.hasKey("refresh:" + oldTokenId)) {
            throw new AuthenticationCredentialsNotFoundException("Refresh token has been revoked, please login again");
        }

        String email = jwtUtils.extractUsername(refreshToken);
        ShopUserDetails userDetails = (ShopUserDetails) shopUserDetailsService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String newAccessToken = jwtUtils.generateAccessToken(authentication);
        String newRefreshToken = jwtUtils.generateRefreshToken(authentication);

        redisTemplate.delete("refresh:" + oldTokenId);

        String newTokenId = jwtUtils.extractTokenId(newRefreshToken);
        redisTemplate.opsForValue().set("refresh:" + newTokenId, newRefreshToken, 30, TimeUnit.DAYS);

        JwtResponse jwtResponse = new JwtResponse(userDetails.getId(), newAccessToken);
        return new TokenResponse(jwtResponse, newRefreshToken);
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken != null && jwtUtils.isTokenValid(refreshToken)) {
            String tokenId = jwtUtils.extractTokenId(refreshToken);
            redisTemplate.delete("refresh:" + tokenId);
        }
    }

    @Override
    public JwtResponse exchangeOAuth2Code(String code) {
        String jwt = redisTemplate.opsForValue().getAndDelete("oauth2:code:" + code);
        if (jwt == null) {
            throw new IllegalStateException("Code expired or invalid, please login again");
        }
        UUID userId = jwtUtils.getUserIdFromToken(jwt);
        return new JwtResponse(userId, jwt);
    }
}
