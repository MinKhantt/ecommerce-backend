package com.example.ecommercebackend.service.user;

import com.example.ecommercebackend.dto.UserDto;
import com.example.ecommercebackend.dto.UserSummaryDto;
import com.example.ecommercebackend.dto.request.CreateUserRequest;
import com.example.ecommercebackend.dto.request.UserUpdateRequest;
import com.example.ecommercebackend.dto.response.PageResponse;
import com.example.ecommercebackend.entity.Role;
import com.example.ecommercebackend.entity.User;
import com.example.ecommercebackend.exception.AlreadyExistsException;
import com.example.ecommercebackend.exception.ResourceNotFoundException;
import com.example.ecommercebackend.mapper.UserMapper;
import com.example.ecommercebackend.repository.RoleRepository;
import com.example.ecommercebackend.repository.UserRepository;
import com.example.ecommercebackend.service.cart.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
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

    @Override
    public PageResponse<UserDto> getAllUsers(Pageable pageable) {
        log.info("Fetching users page {} with size {}", pageable.getPageNumber(), pageable.getPageSize());
        return PageResponse.from(userRepository.findAll(pageable)
                .map(userMapper::userToUserDto));
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
    public UserSummaryDto getAuthenticatedUser() {
        log.info("Authenticating user and fetching from cache");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .map(userMapper::toUserSummaryDto)
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
