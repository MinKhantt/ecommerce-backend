package com.example.shoppingcartapi.security.jwt;

import com.example.shoppingcartapi.security.user.ShopUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    @Value("${access.token.expiration}")
    private int accessTokenExpiration;

    @Value("${refresh.token.expiration}")
    private int refreshExpirationTime;

    public String generateAccessToken(Authentication authentication) {
        ShopUserDetails userPrincipal = (ShopUserDetails) authentication.getPrincipal();

        List<String> roles = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userPrincipal.getEmail())
                .claim("id", userPrincipal.getId())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + accessTokenExpiration))
                .signWith(key())
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        ShopUserDetails userPrincipal = (ShopUserDetails) authentication.getPrincipal();

        List<String> roles = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userPrincipal.getEmail())
                .claim("id", userPrincipal.getId())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + refreshExpirationTime))
                .signWith(key())
                .compact();
    }

    // generate token for OAuth2
    public String generateOAuthAccessToken(String email, UUID userId, List<String> roles) {
        return Jwts.builder()
                .subject(email)
                .claim("id", userId)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + accessTokenExpiration))
                .signWith(key())
                .compact();
    }

    public String generateOAuthRefreshToken(String email, UUID userId, List<String> roles) {
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(email)
                .claim("id", userId)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + refreshExpirationTime))
                .signWith(key())
                .compact();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractTokenId(String token) {
        return extractAllClaims(token).getId();
    }

    public UUID getUserIdFromToken(String token) {
        String id = extractAllClaims(token).get("id", String.class);
        return UUID.fromString(id);
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
