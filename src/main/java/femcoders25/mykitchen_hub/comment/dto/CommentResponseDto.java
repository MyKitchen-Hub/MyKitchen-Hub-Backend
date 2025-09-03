package femcoders25.mykitchen_hub.comment.dto;

import java.time.LocalDateTime;

public record CommentResponseDto(
        Long id,
        String text,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String username) {
}
