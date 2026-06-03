package com.example.shoppingcartapi.service.user;

import com.example.shoppingcartapi.dto.UserDto;
import com.example.shoppingcartapi.dto.request.CreateUserRequest;
import com.example.shoppingcartapi.dto.request.UserUpdateRequest;
import com.example.shoppingcartapi.dto.response.UserListResponse;

import java.util.UUID;

public interface IUserService {

    UserDto createUser(CreateUserRequest request);

    UserDto getUserById(UUID userId);

    UserListResponse getAllUsers();

    UserDto getUserByEmail(String email);

    UserDto updateUser(UserUpdateRequest request, UUID userId);

    void deleteUser(UUID userId);

    UserDto getAuthenticatedUser();
}
