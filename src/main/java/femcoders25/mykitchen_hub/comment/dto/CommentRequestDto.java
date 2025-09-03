package femcoders25.mykitchen_hub.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequestDto(
        @NotBlank(message = "Comment text is required") @Size(min = 1, max = 1000, message = "Comment must be between 1 and 1000 characters")
        String text) {}
