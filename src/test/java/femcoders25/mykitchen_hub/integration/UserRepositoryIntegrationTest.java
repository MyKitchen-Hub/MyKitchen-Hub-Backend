package femcoders25.mykitchen_hub.integration;

import femcoders25.mykitchen_hub.user.entity.Role;
import femcoders25.mykitchen_hub.user.entity.User;
import femcoders25.mykitchen_hub.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.USER);
        entityManager.persistAndFlush(testUser);
    }

    @Test
    void findByUsername_ExistingUser_ReturnsUser() {
        Optional<User> result = userRepository.findByUsername("testuser");
        
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void findByUsername_NonExistingUser_ReturnsEmpty() {
        Optional<User> result = userRepository.findByUsername("nonexistent");
        
        assertFalse(result.isPresent());
    }

    @Test
    void findByEmail_ExistingEmail_ReturnsUser() {
        Optional<User> result = userRepository.findByEmail("test@example.com");
        
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void existsByUsername_ExistingUsername_ReturnsTrue() {
        boolean exists = userRepository.existsByUsername("testuser");
        
        assertTrue(exists);
    }

    @Test
    void existsByUsername_NonExistingUsername_ReturnsFalse() {
        boolean exists = userRepository.existsByUsername("nonexistent");
        
        assertFalse(exists);
    }

    @Test
    void existsByEmail_ExistingEmail_ReturnsTrue() {
        boolean exists = userRepository.existsByEmail("test@example.com");
        
        assertTrue(exists);
    }

    @Test
    void save_NewUser_SavesSuccessfully() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("password");
        newUser.setRole(Role.USER);
        
        User saved = userRepository.save(newUser);
        
        assertNotNull(saved.getId());
        assertEquals("newuser", saved.getUsername());
        assertEquals("new@example.com", saved.getEmail());
    }
}
