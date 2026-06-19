package com.example.ecommercebackend.service.user;

import com.example.ecommercebackend.dto.UserDto;
import com.example.ecommercebackend.dto.request.CreateUserRequest;
import com.example.ecommercebackend.dto.request.UserUpdateRequest;
import com.example.ecommercebackend.exception.AlreadyExistsException;
import com.example.ecommercebackend.mapper.UserMapper;
import com.example.ecommercebackend.entity.User;
import com.example.ecommercebackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Test")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService; // this is the class that we want to test

    private User testUser;
    private UserDto testUserDto;
    private CreateUserRequest testCreateUserRequest;
    private UserUpdateRequest testUserUpdateRequest;

    @BeforeEach
    void setUp() {
        testCreateUserRequest = new CreateUserRequest();
        testCreateUserRequest.setFirstName("Min");
        testCreateUserRequest.setLastName("Khant");
        testCreateUserRequest.setEmail("minkhant@gmail.com");
        testCreateUserRequest.setPassword("minkhant12345");

        testUser =  new User();
        testUser.setId(UUID.randomUUID());
        testUser.setFirstName("Min");
        testUser.setLastName("Khant");
        testUser.setEmail("minkhat@gmail.com");
        testUser.setPassword("minkhant12345");

        testUserDto =  new UserDto();
        testUserDto.setId(UUID.randomUUID());
        testUserDto.setFirstName("Min");
        testUserDto.setLastName("Khant");
        testUserDto.setEmail("minkhant@gmail.com");

        testUserUpdateRequest =  new UserUpdateRequest();
        testUserUpdateRequest.setFirstName("Scott");
        testUserUpdateRequest.setLastName("Scott");
    }

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests{

        @Test
        void shouldCreateUserSuccessfully(){

            // Given
            when(userRepository.existsByEmail(testCreateUserRequest.getEmail()))
                    .thenReturn(false);

            when(userMapper.toUser(testCreateUserRequest))
                    .thenReturn(testUser);

            when(passwordEncoder.encode(testCreateUserRequest.getPassword()))
                    .thenReturn("encodedPassword");

            when(userRepository.save(testUser)).thenReturn(testUser);

            when(userMapper.userToUserDto(testUser)).thenReturn(testUserDto);

            // When
            UserDto result = userService.createUser(testCreateUserRequest);

            // Then
            assertNotNull(result);
            assertEquals(testUserDto.getEmail(), result.getEmail());
//            assertEquals(testCreateUserRequest.getEmail(), result.getEmail());

            verify(userRepository).existsByEmail(testCreateUserRequest.getEmail());
            verify(userMapper.toUser(testCreateUserRequest));
            verify(passwordEncoder).encode(testCreateUserRequest.getPassword());
            verify(userRepository).save(testUser);
            verify(userMapper).userToUserDto(testUser);
        }

        @Test
        void shouldNotCreateUserSuccessfully(){
            // Given
            when(userRepository.existsByEmail(testCreateUserRequest.getEmail())).thenReturn(true);
//            when(userMapper.toUser(testCreateUserRequest)).thenReturn(testUser);

            // When
            UserDto result = userService.createUser(testCreateUserRequest);

            // Then
            assertNull(result);
            verify(userRepository).existsByEmail(testCreateUserRequest.getEmail());
            verify(userRepository, never()).save(any());
            verify(passwordEncoder, never()).encode(any());
            verify(userMapper, never()).toUser(any());
        }

        @Test
        void shouldThrowExceptionWhenEmailAlreadyExists(){
            // Given
            when(userRepository.existsByEmail(testCreateUserRequest.getEmail())).thenReturn(true);

            AlreadyExistsException exception = assertThrows(
                    AlreadyExistsException.class,
                    () -> userService.createUser(testCreateUserRequest)
            );

            assertTrue(exception.getMessage().contains("already exists"));

            verify(userRepository).existsByEmail(testCreateUserRequest.getEmail());
            verify(userMapper, never()).toUser(any());
            verify(passwordEncoder, never()).encode(any());
            verify(userRepository, never()).save(any());
        }
    }
}