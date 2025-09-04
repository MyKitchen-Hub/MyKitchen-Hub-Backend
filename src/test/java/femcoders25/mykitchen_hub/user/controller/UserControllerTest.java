package femcoders25.mykitchen_hub.user.controller;

import femcoders25.mykitchen_hub.common.dto.ApiResponse;
import femcoders25.mykitchen_hub.user.dto.UserMapper;
import femcoders25.mykitchen_hub.user.dto.UserRegistrationDto;
import femcoders25.mykitchen_hub.user.dto.UserResponseDto;
import femcoders25.mykitchen_hub.user.dto.UserUpdateDto;
import femcoders25.mykitchen_hub.user.entity.Role;
import femcoders25.mykitchen_hub.user.entity.User;
import femcoders25.mykitchen_hub.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private UserResponseDto testUserResponseDto;
    private UserRegistrationDto testRegistrationDto;
    private UserUpdateDto testUpdateDto;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.USER);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testUserResponseDto = new UserResponseDto(
                1L, "testuser", "test@example.com",
                Role.USER, LocalDateTime.now(), LocalDateTime.now());

        testRegistrationDto = new UserRegistrationDto(
                "newuser", "newuser@example.com", "password123");

        testUpdateDto = new UserUpdateDto(
                "updateduser", "updated@example.com", "newpassword123");

        pageable = Pageable.unpaged();
    }

    @Test
    void createUser_Success() {
        when(userService.createUser(any(UserRegistrationDto.class))).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponseDto);

        ResponseEntity<ApiResponse<UserResponseDto>> response = userController.createUser(
                new UserRegistrationDto("testuser", "test@example.com", "password123"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("User created successfully", response.getBody().getMessage());
        assertEquals(testUserResponseDto, response.getBody().getData());

        verify(userService).createUser(any(UserRegistrationDto.class));
        verify(userMapper).toResponse(testUser);
    }

    @Test
    void getAllUsers_Success() {
        Page<UserResponseDto> userPage = new PageImpl<>(List.of(testUserResponseDto));
        when(userService.getAllUsers(pageable)).thenReturn(userPage);

        ResponseEntity<ApiResponse<Page<UserResponseDto>>> response = userController.getAllUsers(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(userPage, response.getBody().getData());

        verify(userService).getAllUsers(pageable);
    }

    @Test
    void getUserById_Success() {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponseDto);

        ResponseEntity<ApiResponse<UserResponseDto>> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testUserResponseDto, response.getBody().getData());

        verify(userService).getUserById(1L);
        verify(userMapper).toResponse(testUser);
    }

    @Test
    void updateUser_Success() {
        when(userService.updateUser(eq(1L), any(UserUpdateDto.class))).thenReturn(testUserResponseDto);

        ResponseEntity<ApiResponse<UserResponseDto>> response = userController.updateUser(1L,
                new UserUpdateDto("updateduser", "updated@example.com", "newpassword123"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("User updated successfully", response.getBody().getMessage());
        assertEquals(testUserResponseDto, response.getBody().getData());

        verify(userService).updateUser(eq(1L), any(UserUpdateDto.class));
    }

    @Test
    void deleteUser_Success() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<ApiResponse<String>> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("User deleted successfully", response.getBody().getMessage());
        assertEquals("User with ID 1 has been deleted", response.getBody().getData());

        verify(userService).deleteUser(1L);
    }
}
