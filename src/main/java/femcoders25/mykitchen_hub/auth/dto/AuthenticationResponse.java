package femcoders25.mykitchen_hub.auth.dto;

public record AuthenticationResponse(
        String accessToken,
        String refreshToken) {
}
