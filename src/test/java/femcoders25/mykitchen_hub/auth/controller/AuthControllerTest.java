package femcoders25.mykitchen_hub.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import femcoders25.mykitchen_hub.auth.dto.AuthenticationRequest;
import femcoders25.mykitchen_hub.auth.dto.AuthenticationResponse;
import femcoders25.mykitchen_hub.auth.service.AuthenticationService;
import femcoders25.mykitchen_hub.user.dto.UserRegistrationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

        @Mock
        private AuthenticationService authenticationService;

        @InjectMocks
        private AuthController authController;

        private MockMvc mockMvc;
        private ObjectMapper objectMapper;

        private UserRegistrationDto validRegistrationDto;
        private AuthenticationRequest validAuthRequest;
        private AuthenticationResponse authResponse;
        private String jwtToken;

        @BeforeEach
        void setUp() {
                objectMapper = new ObjectMapper();
                LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
                validator.afterPropertiesSet();

                mockMvc = MockMvcBuilders
                                .standaloneSetup(authController)
                                .setValidator(validator)
                                .build();

                validRegistrationDto = new UserRegistrationDto(
                                "testuser",
                                "test@example.com",
                                "password123");

                validAuthRequest = new AuthenticationRequest(
                                "testuser",
                                "password123");

                jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYxNjI0NzI5MCwiZXhwIjoxNjE2MjUwODkwfQ.signature";
                authResponse = new AuthenticationResponse(jwtToken);
        }

        @Test
        void register_Success() throws Exception {
                when(authenticationService.register(any(UserRegistrationDto.class)))
                                .thenReturn(authResponse);

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRegistrationDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("User registered successfully"))
                                .andExpect(jsonPath("$.data.token").value(jwtToken));

                verify(authenticationService).register(any(UserRegistrationDto.class));
        }

        @Test
        void register_WithInvalidUsername_ReturnsBadRequest() throws Exception {
                UserRegistrationDto invalidDto = new UserRegistrationDto(
                                "ab",
                                "test@example.com",
                                "password123");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDto)))
                                .andExpect(status().isBadRequest());

                verify(authenticationService, never()).register(any());
        }

        @Test
        void register_WithInvalidEmail_ReturnsBadRequest() throws Exception {
                UserRegistrationDto invalidDto = new UserRegistrationDto(
                                "testuser",
                                "invalid-email",
                                "password123");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDto)))
                                .andExpect(status().isBadRequest());

                verify(authenticationService, never()).register(any());
        }

        @Test
        void register_WithInvalidPassword_ReturnsBadRequest() throws Exception {
                UserRegistrationDto invalidDto = new UserRegistrationDto(
                                "testuser",
                                "test@example.com",
                                "123");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDto)))
                                .andExpect(status().isBadRequest());

                verify(authenticationService, never()).register(any());
        }

        @Test
        void authenticate_Success() throws Exception {
                when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                                .thenReturn(authResponse);

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validAuthRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("User authenticated successfully"))
                                .andExpect(jsonPath("$.data.token").value(jwtToken));

                verify(authenticationService).authenticate(any(AuthenticationRequest.class));
        }

        @Test
        void logout_Success() throws Exception {
                try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(
                                SecurityContextHolder.class)) {
                        Authentication mockAuth = mock(Authentication.class);
                        when(mockAuth.getName()).thenReturn("testuser");

                        SecurityContext mockContext = mock(SecurityContext.class);
                        when(mockContext.getAuthentication()).thenReturn(mockAuth);
                        mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(mockContext);

                        mockMvc.perform(post("/api/auth/logout"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.success").value(true))
                                        .andExpect(jsonPath("$.message").value("User logged out successfully"))
                                        .andExpect(jsonPath("$.data").value("Logout successful"));

                        verify(authenticationService).logout("testuser");
                }
        }

        @Test
        void logout_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
                try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(
                                SecurityContextHolder.class)) {
                        SecurityContext mockContext = mock(SecurityContext.class);
                        when(mockContext.getAuthentication()).thenReturn(null);
                        mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(mockContext);

                        mockMvc.perform(post("/api/auth/logout"))
                                        .andExpect(status().isUnauthorized())
                                        .andExpect(jsonPath("$.success").value(false))
                                        .andExpect(jsonPath("$.message").value("Authentication required"));

                        verify(authenticationService, never()).logout(anyString());
                }
        }
}