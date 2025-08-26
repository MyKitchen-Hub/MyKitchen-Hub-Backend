package femcoders25.mykitchen_hub.auth.controller;

import femcoders25.mykitchen_hub.auth.dto.AuthenticationRequest;
import femcoders25.mykitchen_hub.auth.dto.AuthenticationResponse;
import femcoders25.mykitchen_hub.auth.service.AuthenticationService;
import femcoders25.mykitchen_hub.common.dto.ApiResponse;
import femcoders25.mykitchen_hub.user.dto.UserRegistrationDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(
            @Valid @RequestBody UserRegistrationDto request) {
        log.info("Registration request for user: {}", request.username());
        AuthenticationResponse response = authenticationService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @Valid @RequestBody AuthenticationRequest request) {
        log.info("Login request for user: {}", request.username());
        AuthenticationResponse response = authenticationService.authenticate(request);
        return ResponseEntity.ok(ApiResponse.success("User authenticated successfully", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        log.info("Logout request for user: {}", username);
        authenticationService.logout(username);

        return ResponseEntity.ok(ApiResponse.success("User logged out successfully", "Logout successful"));
    }
}
