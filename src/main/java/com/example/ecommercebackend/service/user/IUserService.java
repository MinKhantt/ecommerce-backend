package com.example.ecommercebackend.service.user;

import com.example.ecommercebackend.dto.UserDto;
import com.example.ecommercebackend.dto.UserSummaryDto;
import com.example.ecommercebackend.dto.request.CreateUserRequest;
import com.example.ecommercebackend.dto.request.UserUpdateRequest;
import com.example.ecommercebackend.dto.response.UserListResponse;

import java.util.UUID;

public interface IUserService {

    UserDto createUser(CreateUserRequest request);

    UserDto getUserById(UUID userId);

    UserListResponse getAllUsers();

    UserDto getUserByEmail(String email);

    UserDto updateUser(UserUpdateRequest request, UUID userId);

    void deleteUser(UUID userId);

    UserSummaryDto getAuthenticatedUser();
}
