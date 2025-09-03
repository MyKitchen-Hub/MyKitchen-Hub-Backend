package femcoders25.mykitchen_hub.comment.dto;

import femcoders25.mykitchen_hub.comment.entity.Comment;

public class CommentMapper {

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getText(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getUser().getUsername());
    }
}
