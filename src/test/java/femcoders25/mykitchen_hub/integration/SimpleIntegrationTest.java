package femcoders25.mykitchen_hub.integration;

import femcoders25.mykitchen_hub.user.dto.UserRegistrationDto;
import femcoders25.mykitchen_hub.user.entity.Role;
import femcoders25.mykitchen_hub.user.entity.User;
import femcoders25.mykitchen_hub.user.repository.UserRepository;
import femcoders25.mykitchen_hub.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SimpleIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private femcoders25.mykitchen_hub.email.EmailService emailService;

    private UserRegistrationDto validRegistrationDto;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        validRegistrationDto = new UserRegistrationDto(
                "testuser",
                "test@example.com",
                "password123");
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
                () -> userService.createUser(validRegistrationDto));

        assertEquals("Username already exists", exception.getMessage());
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
}
