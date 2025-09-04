package femcoders25.mykitchen_hub.integration;

import femcoders25.mykitchen_hub.common.exception.ResourceNotFoundException;
import femcoders25.mykitchen_hub.user.dto.UserRegistrationDto;
import femcoders25.mykitchen_hub.user.dto.UserResponseDto;
import femcoders25.mykitchen_hub.user.entity.Role;
import femcoders25.mykitchen_hub.user.entity.User;
import femcoders25.mykitchen_hub.user.repository.UserRepository;
import femcoders25.mykitchen_hub.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserRegistrationDto validRegistrationDto;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        validRegistrationDto = new UserRegistrationDto(
                "testuser",
                "test@example.com",
                "password123"
        );
    }

    @Test
    void createUser_ValidData_CreatesUserSuccessfully() {
        User createdUser = userService.createUser(validRegistrationDto);

        assertNotNull(createdUser.getId());
        assertEquals("testuser", createdUser.getUsername());
        assertEquals("test@example.com", createdUser.getEmail());
        assertEquals(Role.USER, createdUser.getRole());
        assertTrue(passwordEncoder.matches("password123", createdUser.getPassword()));
    }

    @Test
    void createUser_DuplicateUsername_ThrowsException() {
        userService.createUser(validRegistrationDto);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(validRegistrationDto)
        );

        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void createUser_DuplicateEmail_ThrowsException() {
        userService.createUser(validRegistrationDto);
        UserRegistrationDto duplicateEmailDto = new UserRegistrationDto(
                "anotheruser",
                "test@example.com",
                "password123"
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(duplicateEmailDto)
        );

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void getAllUsers_ReturnsPagedUsers() {
        userService.createUser(validRegistrationDto);
        UserRegistrationDto anotherUser = new UserRegistrationDto(
                "anotheruser",
                "another@example.com",
                "password123"
        );
        userService.createUser(anotherUser);

        Page<UserResponseDto> result = userService.getAllUsers(PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
    }

    @Test
    void findByUsername_ExistingUser_ReturnsUser() {
        User createdUser = userService.createUser(validRegistrationDto);

        var result = userService.findByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals(createdUser.getId(), result.get().getId());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void findByUsername_NonExistingUser_ReturnsEmpty() {
        var result = userService.findByUsername("nonexistent");

        assertFalse(result.isPresent());
    }

    @Test
    void getUserById_ExistingUser_ReturnsUser() {
        User createdUser = userService.createUser(validRegistrationDto);

        User result = userService.getUserById(createdUser.getId());

        assertNotNull(result);
        assertEquals(createdUser.getId(), result.getId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getUserById_NonExistingUser_ThrowsException() {
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(999L)
        );

        assertEquals("User not found with id : '999'", exception.getMessage());
    }
}
