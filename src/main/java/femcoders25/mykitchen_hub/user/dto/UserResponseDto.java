package femcoders25.mykitchen_hub.user.dto;

import femcoders25.mykitchen_hub.user.entity.Role;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
        String username,
        String email,
        Role role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
