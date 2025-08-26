package femcoders25.mykitchen_hub.user.service;

import femcoders25.mykitchen_hub.user.dto.UserMapper;
import femcoders25.mykitchen_hub.user.dto.UserRegistrationDto;
import femcoders25.mykitchen_hub.user.entity.Role;
import femcoders25.mykitchen_hub.user.entity.User;
import femcoders25.mykitchen_hub.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
    void findByUsername_UserExists_ReturnsUser() {
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(savedUser));

        Optional<User> result = userService.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(savedUser, result.get());
        verify(userRepository).findByUsername(username);
    }
}