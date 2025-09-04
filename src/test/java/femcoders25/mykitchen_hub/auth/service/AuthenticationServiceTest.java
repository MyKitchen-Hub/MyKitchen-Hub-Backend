package femcoders25.mykitchen_hub.auth.service;

import femcoders25.mykitchen_hub.auth.dto.AuthenticationRequest;
import femcoders25.mykitchen_hub.auth.dto.AuthenticationResponse;
import femcoders25.mykitchen_hub.auth.dto.RefreshTokenRequest;
import femcoders25.mykitchen_hub.user.dto.UserRegistrationDto;
import femcoders25.mykitchen_hub.user.entity.Role;
import femcoders25.mykitchen_hub.user.entity.User;
import femcoders25.mykitchen_hub.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

        @Mock
        private UserService userService;

        @Mock
        private JwtService jwtService;

        @Mock
        private AuthenticationManager authenticationManager;

        @Mock
        private TokenBlacklistService tokenBlacklistService;

        @InjectMocks
        private AuthenticationService authenticationService;

        private UserRegistrationDto validRegistrationDto;
        private AuthenticationRequest validAuthRequest;
        private User createdUser;
        private String accessToken;
        private String refreshToken;

        @BeforeEach
        void setUp() {
                validRegistrationDto = new UserRegistrationDto(
                                "testuser",
                                "test@example.com",
                                "password123");

                validAuthRequest = new AuthenticationRequest(
                                "testuser",
                                "password123");

                createdUser = new User();
                createdUser.setId(1L);
                createdUser.setUsername("testuser");
                createdUser.setEmail("test@example.com");
                createdUser.setPassword("encodedPassword");
                createdUser.setRole(Role.USER);

                accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYxNjI0NzI5MCwiZXhwIjoxNjE2MjUwODkwfQ.signature";
                refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYxNjI0NzI5MCwiZXhwIjoxNjE2MjUwODkwfQ.refresh";
        }

        @Test
        void register_Success() {
                when(userService.createUser(validRegistrationDto)).thenReturn(createdUser);
                when(jwtService.generateToken(createdUser)).thenReturn(accessToken);
                when(jwtService.generateRefreshToken(createdUser)).thenReturn(refreshToken);

                AuthenticationResponse response = authenticationService.register(validRegistrationDto);

                assertNotNull(response);
                assertEquals(accessToken, response.accessToken());
                assertEquals(refreshToken, response.refreshToken());

                verify(userService).createUser(validRegistrationDto);
                verify(jwtService).generateToken(createdUser);
                verify(jwtService).generateRefreshToken(createdUser);
        }

        @Test
        void authenticate_Success() {
                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenReturn(null);
                when(userService.findByUsername(validAuthRequest.username()))
                                .thenReturn(Optional.of(createdUser));
                when(jwtService.generateToken(createdUser)).thenReturn(accessToken);
                when(jwtService.generateRefreshToken(createdUser)).thenReturn(refreshToken);

                AuthenticationResponse response = authenticationService.authenticate(validAuthRequest);

                assertNotNull(response);
                assertEquals(accessToken, response.accessToken());
                assertEquals(refreshToken, response.refreshToken());

                verify(authenticationManager).authenticate(
                                argThat(token -> token.getPrincipal().equals(validAuthRequest.username()) &&
                                                token.getCredentials().equals(validAuthRequest.password())));
                verify(userService).findByUsername(validAuthRequest.username());
                verify(jwtService).generateToken(createdUser);
                verify(jwtService).generateRefreshToken(createdUser);
        }

        @Test
        void authenticate_AuthenticationFails_ThrowsException() {
                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenThrow(new BadCredentialsException("Bad credentials"));

                BadCredentialsException exception = assertThrows(
                                BadCredentialsException.class,
                                () -> authenticationService.authenticate(validAuthRequest));

                assertEquals("Bad credentials", exception.getMessage());
                verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
                verify(userService, never()).findByUsername(anyString());
                verify(jwtService, never()).generateToken(any());
        }

        @Test
        void logout_Success() {
                String accessToken = "test-access-token";
                String refreshToken = "test-refresh-token";

                authenticationService.logout(accessToken, refreshToken);

                verify(tokenBlacklistService).blacklistToken(accessToken);
                verify(tokenBlacklistService).blacklistToken(refreshToken);
        }

        @Test
        void logout_WithNullRefreshToken_Success() {
                String accessToken = "test-access-token";

                authenticationService.logout(accessToken, null);

                verify(tokenBlacklistService).blacklistToken(accessToken);
                verify(tokenBlacklistService, never()).blacklistToken(null);
        }

        @Test
        void refreshToken_Success() {
                String oldRefreshToken = "old-refresh-token";
                String newAccessToken = "new-access-token";
                String newRefreshToken = "new-refresh-token";
                String username = "testuser";

                when(jwtService.isRefreshTokenValid(oldRefreshToken)).thenReturn(true);
                when(jwtService.extractUsername(oldRefreshToken)).thenReturn(username);
                when(userService.findByUsername(username)).thenReturn(Optional.of(createdUser));
                when(jwtService.generateToken(createdUser)).thenReturn(newAccessToken);
                when(jwtService.generateRefreshToken(createdUser)).thenReturn(newRefreshToken);

                AuthenticationResponse response = authenticationService.refreshToken(
                                new RefreshTokenRequest(oldRefreshToken));

                assertNotNull(response);
                assertEquals(newAccessToken, response.accessToken());
                assertEquals(newRefreshToken, response.refreshToken());

                verify(jwtService).isRefreshTokenValid(oldRefreshToken);
                verify(jwtService).extractUsername(oldRefreshToken);
                verify(userService).findByUsername(username);
                verify(jwtService).generateToken(createdUser);
                verify(jwtService).generateRefreshToken(createdUser);
                verify(tokenBlacklistService).blacklistToken(oldRefreshToken);
        }

        @Test
        void refreshToken_InvalidRefreshToken_ThrowsException() {
                String invalidRefreshToken = "invalid-refresh-token";

                when(jwtService.isRefreshTokenValid(invalidRefreshToken)).thenReturn(false);

                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> authenticationService.refreshToken(
                                                new RefreshTokenRequest(invalidRefreshToken)));

                assertEquals("Invalid refresh token", exception.getMessage());
                verify(jwtService).isRefreshTokenValid(invalidRefreshToken);
                verify(jwtService, never()).extractUsername(anyString());
                verify(userService, never()).findByUsername(anyString());
        }

        @Test
        void refreshToken_UserNotFound_ThrowsException() {
                String validRefreshToken = "valid-refresh-token";
                String username = "nonexistentuser";

                when(jwtService.isRefreshTokenValid(validRefreshToken)).thenReturn(true);
                when(jwtService.extractUsername(validRefreshToken)).thenReturn(username);
                when(userService.findByUsername(username)).thenReturn(Optional.empty());

                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> authenticationService.refreshToken(
                                                new RefreshTokenRequest(validRefreshToken)));

                assertEquals("User not found", exception.getMessage());
                verify(jwtService).isRefreshTokenValid(validRefreshToken);
                verify(jwtService).extractUsername(validRefreshToken);
                verify(userService).findByUsername(username);
                verify(jwtService, never()).generateToken(any());
        }

        @Test
        void authenticate_UserNotFound_ThrowsException() {
                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenReturn(null);
                when(userService.findByUsername(validAuthRequest.username()))
                                .thenReturn(Optional.empty());

                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> authenticationService.authenticate(validAuthRequest));

                assertEquals("User not found", exception.getMessage());
                verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
                verify(userService).findByUsername(validAuthRequest.username());
                verify(jwtService, never()).generateToken(any());
        }
}