package femcoders25.mykitchen_hub.comment.service;

import femcoders25.mykitchen_hub.comment.dto.CommentMapper;
import femcoders25.mykitchen_hub.comment.dto.CommentRequestDto;
import femcoders25.mykitchen_hub.comment.dto.CommentResponseDto;
import femcoders25.mykitchen_hub.comment.entity.Comment;
import femcoders25.mykitchen_hub.comment.repository.CommentRepository;
import femcoders25.mykitchen_hub.common.exception.ResourceNotFoundException;
import femcoders25.mykitchen_hub.common.exception.UnauthorizedOperationException;
import femcoders25.mykitchen_hub.recipe.entity.Recipe;
import femcoders25.mykitchen_hub.recipe.repository.RecipeRepository;
import femcoders25.mykitchen_hub.user.entity.User;
import femcoders25.mykitchen_hub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserService userService;
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponseDto createComment(Long recipeId, CommentRequestDto requestDto) {
        User currentUser = userService.getCurrentUser();

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", recipeId));

        Comment comment = new Comment();
        comment.setText(requestDto.text());
        comment.setRecipe(recipe);
        comment.setUser(currentUser);

        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toCommentResponseDto(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByRecipeId(Long recipeId) {
        List<Comment> comments = commentRepository.findByRecipeIdOrderByCreatedAtDesc(recipeId);
        return comments.stream()
                .map(CommentMapper::toCommentResponseDto)
                .toList();
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        User currentUser = userService.getCurrentUser();
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedOperationException("delete", "comment");
        }

        commentRepository.deleteById(id);
    }
}
