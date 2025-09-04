package femcoders25.mykitchen_hub.user.service;

import femcoders25.mykitchen_hub.common.exception.ResourceNotFoundException;
import femcoders25.mykitchen_hub.user.dto.UserMapper;
import femcoders25.mykitchen_hub.user.dto.UserRegistrationDto;
import femcoders25.mykitchen_hub.user.dto.UserResponseDto;
import femcoders25.mykitchen_hub.user.dto.UserUpdateDto;
import femcoders25.mykitchen_hub.user.entity.Role;
import femcoders25.mykitchen_hub.user.entity.User;
import femcoders25.mykitchen_hub.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegistrationDto validRegistrationDto;
    private User savedUser;
    private User createdUser;
    private UserUpdateDto validUpdateDto;
    private UserResponseDto testUserResponseDto;

    @BeforeEach
    void setUp() {
        validRegistrationDto = new UserRegistrationDto(
                "testuser",
                "test@example.com",
                "password123");

        createdUser = new User();
        createdUser.setUsername("testuser");
        createdUser.setEmail("test@example.com");
        createdUser.setPassword("encodedPassword");
        createdUser.setRole(Role.USER);

        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(Role.USER);
        savedUser.setRecipes(new ArrayList<>());
        savedUser.setShoppingLists(new ArrayList<>());

        testUserResponseDto = new UserResponseDto(1L, "testuser", "test@example.com", Role.USER, null, null);

        validUpdateDto = new UserUpdateDto(
                "newusername",
                "newemail@example.com",
                "newpassword123");
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByUsername(validRegistrationDto.username())).thenReturn(false);
        when(userRepository.existsByEmail(validRegistrationDto.email())).thenReturn(false);
        when(passwordEncoder.encode(validRegistrationDto.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser(validRegistrationDto);

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getUsername(), result.getUsername());
        assertEquals(savedUser.getEmail(), result.getEmail());
        assertEquals(savedUser.getRole(), result.getRole());

        verify(userRepository).existsByUsername(validRegistrationDto.username());
        verify(userRepository).existsByEmail(validRegistrationDto.email());
        verify(passwordEncoder).encode(validRegistrationDto.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_UsernameAlreadyExists_ThrowsException() {
        when(userRepository.existsByUsername(validRegistrationDto.username())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(validRegistrationDto));

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository).existsByUsername(validRegistrationDto.username());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmailAlreadyExists_ThrowsException() {
        when(userRepository.existsByUsername(validRegistrationDto.username())).thenReturn(false);
        when(userRepository.existsByEmail(validRegistrationDto.email())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(validRegistrationDto));

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository).existsByUsername(validRegistrationDto.username());
        verify(userRepository).existsByEmail(validRegistrationDto.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllUsers_Success() {
        Pageable pageable = Pageable.unpaged();
        List<User> users = List.of(savedUser);
        Page<User> userPage = new PageImpl<>(users);
        List<UserResponseDto> userResponseDtos = List.of(testUserResponseDto);
        Page<UserResponseDto> expectedPage = new PageImpl<>(userResponseDtos);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toResponse(savedUser)).thenReturn(testUserResponseDto);

        Page<UserResponseDto> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(expectedPage.getContent().size(), result.getContent().size());
        assertEquals(expectedPage.getContent().get(0).username(), result.getContent().get(0).username());

        verify(userRepository).findAll(pageable);
        verify(userMapper).toResponse(savedUser);
    }

    @Test
    void getAllUsers_EmptyPage_ReturnsEmptyPage() {
        Pageable pageable = Pageable.unpaged();
        Page<User> emptyUserPage = new PageImpl<>(new ArrayList<>());
        when(userRepository.findAll(pageable)).thenReturn(emptyUserPage);

        Page<UserResponseDto> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());

        verify(userRepository).findAll(pageable);
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    void findByUsername_UserExists_ReturnsUser() {
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(savedUser));

        Optional<User> result = userService.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(savedUser, result.get());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(savedUser));

        User result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getUsername(), result.getUsername());
        assertEquals(savedUser.getEmail(), result.getEmail());
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_UserDoesNotExist_ThrowsException() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(userId));

        assertEquals("User not found with id : '999'", exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(userRepository.existsByUsername(validUpdateDto.username())).thenReturn(false);
        when(userRepository.existsByEmail(validUpdateDto.email())).thenReturn(false);
        when(passwordEncoder.encode(validUpdateDto.password())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponse(any(User.class)))
                .thenReturn(new UserResponseDto(1L, "newusername", "newemail@example.com", Role.USER, null, null));

        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            Authentication mockAuth = mock(Authentication.class);
            when(mockAuth.getName()).thenReturn("testuser");
            SecurityContext mockContext = mock(SecurityContext.class);
            when(mockContext.getAuthentication()).thenReturn(mockAuth);
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(mockContext);
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(savedUser));

            UserResponseDto result = userService.updateUser(1L, validUpdateDto);

            assertNotNull(result);
            verify(userRepository).save(any(User.class));
        }
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));

        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            Authentication mockAuth = mock(Authentication.class);
            when(mockAuth.getName()).thenReturn("testuser");
            SecurityContext mockContext = mock(SecurityContext.class);
            when(mockContext.getAuthentication()).thenReturn(mockAuth);
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(mockContext);
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(savedUser));

            assertDoesNotThrow(() -> userService.deleteUser(1L));
            verify(userRepository).delete(savedUser);
        }
    }

    @Test
    void isCurrentUser_WithValidUserId_ReturnsTrue() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            Authentication mockAuth = mock(Authentication.class);
            when(mockAuth.getName()).thenReturn("testuser");

            SecurityContext mockContext = mock(SecurityContext.class);
            when(mockContext.getAuthentication()).thenReturn(mockAuth);
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(mockContext);

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(savedUser));

            boolean result = userService.isCurrentUser(1L);

            assertTrue(result);
        }
    }

    @Test
    void isCurrentUser_WithInvalidUserId_ReturnsFalse() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            Authentication mockAuth = mock(Authentication.class);
            when(mockAuth.getName()).thenReturn("testuser");

            SecurityContext mockContext = mock(SecurityContext.class);
            when(mockContext.getAuthentication()).thenReturn(mockAuth);
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(mockContext);

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(savedUser));

            boolean result = userService.isCurrentUser(999L);

            assertFalse(result);
        }
    }

    @Test
    void getCurrentUser_Success() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            Authentication mockAuth = mock(Authentication.class);
            when(mockAuth.getName()).thenReturn("testuser");

            SecurityContext mockContext = mock(SecurityContext.class);
            when(mockContext.getAuthentication()).thenReturn(mockAuth);
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(mockContext);

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(savedUser));

            User result = userService.getCurrentUser();

            assertNotNull(result);
            assertEquals(savedUser, result);
        }
    }

    @Test
    void getCurrentUserId_Success() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            Authentication mockAuth = mock(Authentication.class);
            when(mockAuth.getName()).thenReturn("testuser");

            SecurityContext mockContext = mock(SecurityContext.class);
            when(mockContext.getAuthentication()).thenReturn(mockAuth);
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(mockContext);

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(savedUser));

            Long result = userService.getCurrentUserId();

            assertEquals(1L, result);
        }
    }

    @Test
    void getCurrentUserOptional_WithAuthenticatedUser_ReturnsUser() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            Authentication mockAuth = mock(Authentication.class);
            when(mockAuth.getName()).thenReturn("testuser");
            when(mockAuth.isAuthenticated()).thenReturn(true);

            SecurityContext mockContext = mock(SecurityContext.class);
            when(mockContext.getAuthentication()).thenReturn(mockAuth);
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(mockContext);

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(savedUser));

            Optional<User> result = userService.getCurrentUserOptional();

            assertTrue(result.isPresent());
            assertEquals(savedUser, result.get());
        }
    }

    @Test
    void getCurrentUserOptional_WithAnonymousUser_ReturnsEmpty() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            Authentication mockAuth = mock(Authentication.class);
            when(mockAuth.getName()).thenReturn("anonymousUser");
            when(mockAuth.isAuthenticated()).thenReturn(true);

            SecurityContext mockContext = mock(SecurityContext.class);
            when(mockContext.getAuthentication()).thenReturn(mockAuth);
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(mockContext);

            Optional<User> result = userService.getCurrentUserOptional();

            assertFalse(result.isPresent());
        }
    }

    @Test
    void getCurrentUserIdOptional_WithAuthenticatedUser_ReturnsUserId() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            Authentication mockAuth = mock(Authentication.class);
            when(mockAuth.getName()).thenReturn("testuser");
            when(mockAuth.isAuthenticated()).thenReturn(true);

            SecurityContext mockContext = mock(SecurityContext.class);
            when(mockContext.getAuthentication()).thenReturn(mockAuth);
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(mockContext);

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(savedUser));

            Optional<Long> result = userService.getCurrentUserIdOptional();

            assertTrue(result.isPresent());
            assertEquals(1L, result.get());
        }
    }

    @Test
    void updateUser_WithNullUpdateDto_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(1L, null));

        assertEquals("Update data cannot be null", exception.getMessage());
    }

    @Test
    void findByUsername_UserDoesNotExist_ReturnsEmpty() {
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername(username);

        assertFalse(result.isPresent());
        verify(userRepository).findByUsername(username);
    }
}