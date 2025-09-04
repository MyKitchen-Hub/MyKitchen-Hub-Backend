package femcoders25.mykitchen_hub.comment.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentService commentService;

    private User user;
    private Recipe recipe;
    private Comment comment;
    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Test Recipe");

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Great recipe!");
        comment.setUser(user);
        comment.setRecipe(recipe);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        commentRequestDto = new CommentRequestDto("Great recipe!");
        commentResponseDto = new CommentResponseDto(
                1L,
                "Great recipe!",
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                "testuser");
    }

    @Test
    void testCreateComment_Success() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponseDto result = commentService.createComment(1L, commentRequestDto);

        assertNotNull(result);
        assertEquals(commentResponseDto.id(), result.id());
        assertEquals(commentResponseDto.text(), result.text());
        assertEquals(commentResponseDto.username(), result.username());

        verify(userService).getCurrentUser();
        verify(recipeRepository).findById(1L);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testCreateComment_RecipeNotFound() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(recipeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(1L, commentRequestDto));

        verify(userService).getCurrentUser();
        verify(recipeRepository).findById(1L);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void testGetCommentsByRecipeId_Success() {
        List<Comment> comments = List.of(comment);
        when(commentRepository.findByRecipeIdOrderByCreatedAtDesc(1L)).thenReturn(comments);

        List<CommentResponseDto> result = commentService.getCommentsByRecipeId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(commentResponseDto.id(), result.get(0).id());
        assertEquals(commentResponseDto.text(), result.get(0).text());

        verify(commentRepository).findByRecipeIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void testGetCommentsByRecipeId_EmptyList() {
        when(commentRepository.findByRecipeIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());

        List<CommentResponseDto> result = commentService.getCommentsByRecipeId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(commentRepository).findByRecipeIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void testGetCommentsByRecipeId_MultipleComments() {
        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setText("Another great recipe!");
        comment2.setUser(user);
        comment2.setRecipe(recipe);
        comment2.setCreatedAt(LocalDateTime.now().minusHours(1));
        comment2.setUpdatedAt(LocalDateTime.now().minusHours(1));

        List<Comment> comments = List.of(comment, comment2);
        when(commentRepository.findByRecipeIdOrderByCreatedAtDesc(1L)).thenReturn(comments);

        List<CommentResponseDto> result = commentService.getCommentsByRecipeId(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(comment.getId(), result.get(0).id());
        assertEquals(comment2.getId(), result.get(1).id());

        verify(commentRepository).findByRecipeIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void testDeleteComment_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userService.getCurrentUser()).thenReturn(user);

        commentService.deleteComment(1L);

        verify(commentRepository).findById(1L);
        verify(userService).getCurrentUser();
        verify(commentRepository).deleteById(1L);
    }

    @Test
    void testDeleteComment_CommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(1L));

        verify(commentRepository).findById(1L);
        verify(userService, never()).getCurrentUser();
        verify(commentRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteComment_UnauthorizedUser() {
        User otherUser = new User();
        otherUser.setId(999L);
        otherUser.setUsername("otheruser");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userService.getCurrentUser()).thenReturn(otherUser);

        assertThrows(UnauthorizedOperationException.class, () -> commentService.deleteComment(1L));

        verify(commentRepository).findById(1L);
        verify(userService).getCurrentUser();
        verify(commentRepository, never()).deleteById(anyLong());
    }
}
