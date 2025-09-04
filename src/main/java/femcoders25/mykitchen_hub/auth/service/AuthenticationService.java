package femcoders25.mykitchen_hub.auth.service;

import femcoders25.mykitchen_hub.auth.dto.AuthenticationRequest;
import femcoders25.mykitchen_hub.auth.dto.AuthenticationResponse;
import femcoders25.mykitchen_hub.auth.dto.RefreshTokenRequest;
import femcoders25.mykitchen_hub.user.dto.UserRegistrationDto;
import femcoders25.mykitchen_hub.user.entity.User;
import femcoders25.mykitchen_hub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthenticationResponse register(UserRegistrationDto request) {
        User user = userService.createUser(request);
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("User registered successfully: {}", user.getUsername());
        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()));

        UserDetails user = userService.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("User authenticated successfully: {}", user.getUsername());
        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public void logout(String accessToken, String refreshToken) {
        tokenBlacklistService.blacklistToken(accessToken);
        if (refreshToken != null) {
            tokenBlacklistService.blacklistToken(refreshToken);
        }
        log.info("User logged out successfully");
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();
        
        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);
        UserDetails user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        tokenBlacklistService.blacklistToken(refreshToken);

        log.info("Token refreshed successfully for user: {}", username);
        return new AuthenticationResponse(newAccessToken, newRefreshToken);
    }
}