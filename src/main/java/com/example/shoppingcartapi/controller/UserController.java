package com.example.shoppingcartapi.controller;

import com.example.shoppingcartapi.dto.UserDto;
import com.example.shoppingcartapi.dto.request.CreateUserRequest;
import com.example.shoppingcartapi.dto.request.UserUpdateRequest;
import com.example.shoppingcartapi.dto.response.ApiResponse;
import com.example.shoppingcartapi.dto.response.UserListResponse;
import com.example.shoppingcartapi.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final IUserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDto userDto = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("User created successfully", userDto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId) {
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(new ApiResponse("User fetched successfully", userDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers() {
        UserListResponse userListResponse = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse("User fetched successfully", userListResponse));
    }

    @GetMapping("/by-email")
    public  ResponseEntity<ApiResponse> getUserByEmail(@RequestParam String email) {
        UserDto userDto = userService.getUserByEmail(email);
        return ResponseEntity.ok(new ApiResponse("User fetched successfully", userDto));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody UserUpdateRequest request, @PathVariable Long userId) {
        UserDto userDto = userService.updateUser(request, userId);
        return ResponseEntity.ok(new ApiResponse("User updated successfully", userDto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
