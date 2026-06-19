package com.example.ecommercebackend.mapper;

import com.example.ecommercebackend.dto.UserDto;
import com.example.ecommercebackend.dto.request.CreateUserRequest;
import com.example.ecommercebackend.dto.request.UserUpdateRequest;
import com.example.ecommercebackend.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper modelMapper;

    public User toUser(CreateUserRequest request) {
        return modelMapper.map(request, User.class);
    }

    public void updateUserFromRequest(UserUpdateRequest request, User existingUser) {
        modelMapper.map(request, existingUser);
    }

    public UserDto userToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User userDtoToUser(UserDto user) {
        return modelMapper.map(user, User.class);
    }
}
