package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.response.ApiResponse;
import com.example.ecommercebackend.dto.response.JwtResponse;
import com.example.ecommercebackend.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
public class OAuth2Controller {

    private final StringRedisTemplate redisTemplate;
    private final JwtUtils jwtUtils;

    @PostMapping("/oauth2/exchange")
    public ResponseEntity<ApiResponse> exchange(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Missing code", null));
        }

        String jwt = redisTemplate.opsForValue().getAndDelete("oauth2:code:" + code);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .body(new ApiResponse("Code expired or invalid, please login again", null));
        }

        UUID userId = jwtUtils.getUserIdFromToken(jwt);
        JwtResponse jwtResponse = new JwtResponse(userId, jwt);

        return ResponseEntity.ok(new ApiResponse("OK", jwtResponse));
    }
}
