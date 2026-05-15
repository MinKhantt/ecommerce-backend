package com.example.shoppingcartapi.controller;

import com.example.shoppingcartapi.dto.UserDto;
import com.example.shoppingcartapi.dto.request.CreateUserRequest;
import com.example.shoppingcartapi.dto.request.LoginRequest;
import com.example.shoppingcartapi.dto.response.ApiResponse;
import com.example.shoppingcartapi.dto.response.JwtResponse;
import com.example.shoppingcartapi.mapper.UserMapper;
import com.example.shoppingcartapi.security.jwt.JwtUtils;
import com.example.shoppingcartapi.security.user.ShopUserDetails;
import com.example.shoppingcartapi.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody CreateUserRequest request) {
//        try {
//            UserDto user = userService.createUser(request);
//            UserDto userDto = userMapper.UserToUserDto(user);
//            return ResponseEntity.ok(new ApiResponse("Registration Successful", userDto));
//        } catch (AlreadyExistsException e) {
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body(new ApiResponse(e.getMessage(), null));
//        }

        UserDto userDto = userService.createUser(request);
        return ResponseEntity.ok(new ApiResponse("Registration Successful", userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateTokenForUser(authentication);
            ShopUserDetails userDetails = (ShopUserDetails) authentication.getPrincipal();
            JwtResponse jwtResponse = new JwtResponse(userDetails.getId(), jwt);

            return ResponseEntity.ok(
                    new ApiResponse(
                            "Login Successful",
                            jwtResponse
                    )
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse(e.getMessage(), null)
            );
        }
    }
}
