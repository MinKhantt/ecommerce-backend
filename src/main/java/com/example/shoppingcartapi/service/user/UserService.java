package com.example.shoppingcartapi.service.user;

import com.example.shoppingcartapi.dto.UserDto;
import com.example.shoppingcartapi.dto.request.CreateUserRequest;
import com.example.shoppingcartapi.dto.request.UserUpdateRequest;
import com.example.shoppingcartapi.dto.response.UserListResponse;
import com.example.shoppingcartapi.exception.AlreadyExistsException;
import com.example.shoppingcartapi.exception.ResourceNotFoundException;
import com.example.shoppingcartapi.mapper.UserMapper;
import com.example.shoppingcartapi.entity.User;
import com.example.shoppingcartapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @CacheEvict(value = "users", key = "'all_users'")
    @Override
    public UserDto createUser(CreateUserRequest request) {
        log.info("Attempting to create a new user account");
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Creating new user failed: Email: {} already exists.", request.getEmail());
            throw new AlreadyExistsException(
                    "User " + request.getEmail() + " already exists!"
            );
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        log.info("created user with email: {}", request.getEmail());
        return userMapper.userToUserDto(user);
    }

    @Cacheable(value = "users", key = "#userId")
    @Override
    public UserDto getUserById(Long userId) {
        log.info("Fetching user by id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " not found!"));

        return userMapper.userToUserDto(user);
    }

    @Cacheable(value = "users", key = "'all_users'")
    @Override
    public UserListResponse getAllUsers() {
        log.info("Fetching all users from DB");
        List<UserDto> userList = userRepository.findAll()
                .stream()
                .map(userMapper::userToUserDto)
                .toList();

        return new UserListResponse(userList);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        User user = Optional.ofNullable(userRepository.findByEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException("User With Email: " + email + " not found!"));

        return userMapper.userToUserDto(user);
    }

    @Transactional
    @CachePut(value = "users", key = "#userId")
    @CacheEvict(value = "users", key = "'all_users'")
    @Override
    public UserDto updateUser(UserUpdateRequest request, long userId) {
        log.info("Updating user with id: {}", userId);
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " not found!"));

        userMapper.updateUserFromRequest(request, existingUser);
        userRepository.save(existingUser);
        return userMapper.userToUserDto(existingUser);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#userId"),
            @CacheEvict(value = "users", key = "'all_users'")
    })
    @Override
    public void deleteUser(Long userId) {
        log.info("Deleting user with id: {}", userId);
        userRepository.findById(userId)
                        .ifPresentOrElse(userRepository :: delete, () -> {
                            throw new ResourceNotFoundException("User not found");
                        });
    }

    @Override
    public UserDto getAuthenticatedUser() {
        log.info("Authenticating user");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = Optional.ofNullable(userRepository.findByEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        return userMapper.userToUserDto(user);
    }
}
