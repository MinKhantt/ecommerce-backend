package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.response.ApiResponse;
import com.example.ecommercebackend.dto.response.JwtResponse;
import com.example.ecommercebackend.service.auth.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
public class OAuth2Controller {

    private final IAuthService authService;

    @PostMapping("/oauth2/exchange")
    @Operation(summary = "Exchange OAuth2 code", description = "Exchange single-use OAuth2 code for JWT access token")
    public ResponseEntity<ApiResponse> exchange(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Missing code", null));
        }

        JwtResponse jwtResponse = authService.exchangeOAuth2Code(code);
        return ResponseEntity.ok(new ApiResponse("OK", jwtResponse));
    }
}
