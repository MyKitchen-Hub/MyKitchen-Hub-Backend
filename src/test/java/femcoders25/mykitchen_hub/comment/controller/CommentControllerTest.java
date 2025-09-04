package femcoders25.mykitchen_hub.comment.controller;

import femcoders25.mykitchen_hub.comment.dto.CommentRequestDto;
import femcoders25.mykitchen_hub.comment.dto.CommentResponseDto;
import femcoders25.mykitchen_hub.comment.service.CommentService;
import femcoders25.mykitchen_hub.common.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private CommentResponseDto commentResponseDto;
    private List<CommentResponseDto> commentList;

    @BeforeEach
    void setUp() {
        new CommentRequestDto("Great recipe!");
        commentResponseDto = new CommentResponseDto(
                1L,
                "Great recipe!",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "testuser");
        commentList = List.of(commentResponseDto);
    }

    @Test
    void testCreateComment_Success() {
        when(commentService.createComment(eq(1L), any(CommentRequestDto.class))).thenReturn(commentResponseDto);

        ResponseEntity<ApiResponse<CommentResponseDto>> response = commentController.createComment(1L,
                new CommentRequestDto("Great recipe!"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Comment created successfully!", response.getBody().getMessage());
        assertEquals(commentResponseDto, response.getBody().getData());

        verify(commentService).createComment(eq(1L), any(CommentRequestDto.class));
    }

    @Test
    void testCreateComment_WithValidData() {
        when(commentService.createComment(eq(1L), any(CommentRequestDto.class))).thenReturn(commentResponseDto);

        ResponseEntity<ApiResponse<CommentResponseDto>> response = commentController.createComment(1L,
                new CommentRequestDto("This is a valid comment with proper length"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Comment created successfully!", response.getBody().getMessage());

        verify(commentService).createComment(eq(1L), any(CommentRequestDto.class));
    }

    @Test
    void testGetCommentsByRecipeId_Success() {
        when(commentService.getCommentsByRecipeId(1L)).thenReturn(commentList);

        ResponseEntity<ApiResponse<List<CommentResponseDto>>> response = commentController.getCommentsByRecipeId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Comments retrieved successfully", response.getBody().getMessage());
        assertEquals(commentList, response.getBody().getData());

        verify(commentService).getCommentsByRecipeId(1L);
    }

    @Test
    void testGetCommentsByRecipeId_EmptyList() {
        when(commentService.getCommentsByRecipeId(1L)).thenReturn(List.of());

        ResponseEntity<ApiResponse<List<CommentResponseDto>>> response = commentController.getCommentsByRecipeId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Comments retrieved successfully", response.getBody().getMessage());
        assertTrue(response.getBody().getData().isEmpty());

        verify(commentService).getCommentsByRecipeId(1L);
    }

    @Test
    void testGetCommentsByRecipeId_MultipleComments() {
        CommentResponseDto comment2 = new CommentResponseDto(
                2L,
                "Another great comment!",
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusHours(1),
                "anotheruser");
        List<CommentResponseDto> multipleComments = List.of(commentResponseDto, comment2);

        when(commentService.getCommentsByRecipeId(1L)).thenReturn(multipleComments);

        ResponseEntity<ApiResponse<List<CommentResponseDto>>> response = commentController.getCommentsByRecipeId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Comments retrieved successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().size());
        assertEquals(multipleComments, response.getBody().getData());

        verify(commentService).getCommentsByRecipeId(1L);
    }

    @Test
    void testCreateComment_DifferentRecipeIds() {
        when(commentService.createComment(eq(2L), any(CommentRequestDto.class))).thenReturn(commentResponseDto);

        ResponseEntity<ApiResponse<CommentResponseDto>> response = commentController.createComment(2L,
                new CommentRequestDto("Great recipe!"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Comment created successfully!", response.getBody().getMessage());

        verify(commentService).createComment(eq(2L), any(CommentRequestDto.class));
    }

    @Test
    void testGetCommentsByRecipeId_DifferentRecipeIds() {
        when(commentService.getCommentsByRecipeId(2L)).thenReturn(commentList);

        ResponseEntity<ApiResponse<List<CommentResponseDto>>> response = commentController.getCommentsByRecipeId(2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Comments retrieved successfully", response.getBody().getMessage());

        verify(commentService).getCommentsByRecipeId(2L);
    }
}
