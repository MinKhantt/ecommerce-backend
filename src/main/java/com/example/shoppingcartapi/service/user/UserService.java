package com.example.shoppingcartapi.service.user;

import com.example.shoppingcartapi.dto.UserDto;
import com.example.shoppingcartapi.dto.request.CreateUserRequest;
import com.example.shoppingcartapi.dto.request.UserUpdateRequest;
import com.example.shoppingcartapi.dto.response.UserListResponse;
import com.example.shoppingcartapi.entity.Role;
import com.example.shoppingcartapi.exception.AlreadyExistsException;
import com.example.shoppingcartapi.exception.ResourceNotFoundException;
import com.example.shoppingcartapi.mapper.UserMapper;
import com.example.shoppingcartapi.entity.User;
import com.example.shoppingcartapi.repository.RoleRepository;
import com.example.shoppingcartapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

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
        Role role = roleRepository.findByName("ROLE_USER");
        user.setRoles(Set.of(role));
        userRepository.save(user);
        log.info("created user with email: {}", request.getEmail());
        return userMapper.userToUserDto(user);
    }

    @Cacheable(value = "users", key = "#userId")
    @Override
    public UserDto getUserById(UUID userId) {
        log.info("Fetching user by id: {}", userId);

        return userRepository.findById(userId)
                .map(userMapper::userToUserDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
    }

    @Cacheable(value = "users", key = "'all_users'")
    @Override
    public UserListResponse getAllUsers() {
        log.info("Fetching all users.");
        List<UserDto> userList = userRepository.findAll()
                .stream()
                .map(userMapper::userToUserDto)
                .toList();

        return new UserListResponse(userList);
    }

    @Override
    @Cacheable(value = "users", key = "'email_' + #email")
    public UserDto getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);

        return userRepository.findByEmail(email)
                .map(userMapper::userToUserDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    @Transactional
    @Caching(
            put = @CachePut(value = "users", key = "#userId"),
            evict = {
                    @CacheEvict(value = "users", key = "'all_users'"),
                    @CacheEvict(value = "users", key = "'email_' + #result.email"),
                    @CacheEvict(value = "users", key = "'auth_' + #result.email")
            }
    )
    public UserDto updateUser(UserUpdateRequest request, UUID userId) {
        log.info("Updating user with id: {}", userId);
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " not found!"));

        userMapper.updateUserFromRequest(request, existingUser);
        userRepository.save(existingUser);
        return userMapper.userToUserDto(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        log.info("Deleting user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        clearUserCaches(user.getId(), user.getEmail());
        userRepository.delete(user);
    }

    @Override
    @Cacheable(value = "users", key = "'auth_' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public UserDto getAuthenticatedUser() {
        log.info("Authenticating user and fetching from cache");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .map(userMapper::userToUserDto)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    @Caching(evict = {
            @CacheEvict(value = "users", key = "#userId"),
            @CacheEvict(value = "users", key = "'email_' + #email"),
            @CacheEvict(value = "users", key = "'auth_' + #email"),
            @CacheEvict(value = "users", key = "'all_users'")
    })
    private void clearUserCaches(UUID userId, String email) {
        log.info("Evicting caches for user: {}", email);
    }
}
