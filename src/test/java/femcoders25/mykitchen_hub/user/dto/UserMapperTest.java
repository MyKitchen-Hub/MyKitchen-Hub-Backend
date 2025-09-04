package femcoders25.mykitchen_hub.user.dto;

import femcoders25.mykitchen_hub.user.entity.Role;
import femcoders25.mykitchen_hub.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapper userMapper;

    @Test
    void toResponse_WithValidUser_ReturnsUserResponseDto() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        UserResponseDto result = userMapper.toResponse(user);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("testuser", result.username());
        assertEquals("test@example.com", result.email());
        assertEquals(Role.USER, result.role());
    }

    @Test
    void toResponse_WithNullUser_ReturnsNull() {
        UserResponseDto result = userMapper.toResponse(null);
        assertNull(result);
    }

    @Test
    void toEntity_WithValidRegistrationDto_ReturnsUser() {
        UserRegistrationDto dto = new UserRegistrationDto("testuser", "test@example.com", "password123");

        User result = userMapper.toEntity(dto);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    void toEntity_WithNullDto_ReturnsNull() {
        User result = userMapper.toEntity(null);
        assertNull(result);
    }

    @Test
    void updateEntity_WithValidUserAndDto_UpdatesUser() {
        User user = new User();
        user.setUsername("olduser");
        user.setEmail("old@example.com");

        UserUpdateDto dto = new UserUpdateDto("newuser", "new@example.com", "newpassword");

        userMapper.updateEntity(user, dto);

        assertEquals("newuser", user.getUsername());
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    void updateEntity_WithNullUser_DoesNothing() {
        UserUpdateDto dto = new UserUpdateDto("newuser", "new@example.com", "newpassword");

        assertDoesNotThrow(() -> userMapper.updateEntity(null, dto));
    }

    @Test
    void updateEntity_WithNullDto_DoesNothing() {
        User user = new User();
        user.setUsername("testuser");

        assertDoesNotThrow(() -> userMapper.updateEntity(user, null));
    }

    @Test
    void updateEntity_WithPartialDto_UpdatesOnlyProvidedFields() {
        User user = new User();
        user.setUsername("olduser");
        user.setEmail("old@example.com");

        UserUpdateDto dto = new UserUpdateDto("newuser", null, null);

        userMapper.updateEntity(user, dto);

        assertEquals("newuser", user.getUsername());
        assertEquals("old@example.com", user.getEmail());
    }
}

