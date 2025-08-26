package femcoders25.mykitchen_hub.auth.service;

import femcoders25.mykitchen_hub.auth.dto.AuthenticationRequest;
import femcoders25.mykitchen_hub.auth.dto.AuthenticationResponse;
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

        @InjectMocks
        private AuthenticationService authenticationService;

        private UserRegistrationDto validRegistrationDto;
        private AuthenticationRequest validAuthRequest;
        private User createdUser;
        private String jwtToken;

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

                jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYxNjI0NzI5MCwiZXhwIjoxNjE2MjUwODkwfQ.signature";
        }

        @Test
        void register_Success() {
                when(userService.createUser(validRegistrationDto)).thenReturn(createdUser);
                when(jwtService.generateToken(createdUser)).thenReturn(jwtToken);

                AuthenticationResponse response = authenticationService.register(validRegistrationDto);

                assertNotNull(response);
                assertEquals(jwtToken, response.token());

                verify(userService).createUser(validRegistrationDto);
                verify(jwtService).generateToken(createdUser);
        }

        @Test
        void authenticate_Success() {
                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenReturn(null);
                when(userService.findByUsername(validAuthRequest.username()))
                                .thenReturn(Optional.of(createdUser));
                when(jwtService.generateToken(createdUser)).thenReturn(jwtToken);

                AuthenticationResponse response = authenticationService.authenticate(validAuthRequest);

                assertNotNull(response);
                assertEquals(jwtToken, response.token());

                verify(authenticationManager).authenticate(
                                argThat(token -> token.getPrincipal().equals(validAuthRequest.username()) &&
                                                token.getCredentials().equals(validAuthRequest.password())));
                verify(userService).findByUsername(validAuthRequest.username());
                verify(jwtService).generateToken(createdUser);
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
                String username = "testuser";

                authenticationService.logout(username);
        }
}