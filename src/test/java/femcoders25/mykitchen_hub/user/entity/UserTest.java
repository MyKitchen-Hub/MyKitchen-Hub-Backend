package femcoders25.mykitchen_hub.user.entity;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void user_DefaultValues_AreCorrect() {
        User user = new User();

        assertEquals(Role.USER, user.getRole());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    void user_GetAuthorities_ReturnsCorrectRole() {
        User user = new User();
        user.setRole(Role.ADMIN);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void user_GetAuthorities_WithUserRole_ReturnsCorrectRole() {
        User user = new User();
        user.setRole(Role.USER);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void user_OnCreate_SetsTimestamps() {
        User user = new User();
        user.onCreate();

        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    void user_OnUpdate_UpdatesTimestamp() {
        User user = new User();
        user.onCreate();
        LocalDateTime originalUpdatedAt = user.getUpdatedAt();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        user.onUpdate();

        assertNotNull(user.getUpdatedAt());
        assertTrue(user.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void user_AllArgsConstructor_CreatesUserWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(1L, "testuser", "test@example.com", "password123", Role.ADMIN, null, null, null, null, now, now);

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(Role.ADMIN, user.getRole());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    void user_NoArgsConstructor_CreatesEmptyUser() {
        User user = new User();

        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertEquals(Role.USER, user.getRole());
        assertNull(user.getComments());
        assertNull(user.getLikes());
        assertNull(user.getRecipes());
        assertNull(user.getShoppingLists());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }
}

