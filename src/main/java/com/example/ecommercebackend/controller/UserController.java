package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.UserDto;
import com.example.ecommercebackend.dto.request.CreateUserRequest;
import com.example.ecommercebackend.dto.request.UserUpdateRequest;
import com.example.ecommercebackend.dto.response.ApiResponse;
import com.example.ecommercebackend.service.user.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final IUserService userService;

    @PostMapping
    @Operation(summary = "Create user", description = "Register a new user account")
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDto userDto = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("User created successfully", userDto));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieve a single user by their UUID")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable UUID userId) {
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(new ApiResponse("User fetched successfully", userDto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Paginated list of all users, admin only")
    public ResponseEntity<ApiResponse> getAllUsers(
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse("User fetched successfully", userService.getAllUsers(pageable)));
    }

    @GetMapping("/by-email")
    @Operation(summary = "Get user by email", description = "Retrieve a single user by their email address")
    public  ResponseEntity<ApiResponse> getUserByEmail(@RequestParam String email) {
        UserDto userDto = userService.getUserByEmail(email);
        return ResponseEntity.ok(new ApiResponse("User fetched successfully", userDto));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user", description = "Update user details, admin only")
    public ResponseEntity<ApiResponse> updateUser(@Valid @RequestBody UserUpdateRequest request, @PathVariable UUID userId) {
        UserDto userDto = userService.updateUser(request, userId);
        return ResponseEntity.ok(new ApiResponse("User updated successfully", userDto));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Delete a user by ID, admin only")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
