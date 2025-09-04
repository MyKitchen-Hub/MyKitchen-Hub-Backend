package femcoders25.mykitchen_hub.comment.controller;

import femcoders25.mykitchen_hub.comment.dto.CommentRequestDto;
import femcoders25.mykitchen_hub.comment.dto.CommentResponseDto;
import femcoders25.mykitchen_hub.comment.service.CommentService;
import femcoders25.mykitchen_hub.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes/{recipeId}/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Comment management APIs")
public class CommentController {
        private final CommentService commentService;

        @PostMapping
        @PreAuthorize("hasRole('USER')")
        @Operation(summary = "Create a comment for a recipe", description = "Creates a comment for a recipe. Comment text must be 1-1000 characters.")
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Comment data", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentRequestDto.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(name = "Valid comment", value = "{\"text\":\"This recipe looks delicious!\"}")))
        public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
                        @Parameter(description = "Recipe ID") @PathVariable Long recipeId,
                        @Valid @RequestBody CommentRequestDto requestDto) {
                CommentResponseDto comment = commentService.createComment(recipeId, requestDto);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success("Comment created successfully!", comment));
        }

        @GetMapping
        @Operation(summary = "Get all comments for a recipe")
        public ResponseEntity<ApiResponse<List<CommentResponseDto>>> getCommentsByRecipeId(
                        @PathVariable Long recipeId) {
                List<CommentResponseDto> comments = commentService.getCommentsByRecipeId(recipeId);
                return ResponseEntity.ok(ApiResponse.success("Comments retrieved successfully", comments));
        }

        @DeleteMapping("/{commentId}")
        @PreAuthorize("hasRole('USER')")
        @Operation(summary = "Delete a comment")
        public ResponseEntity<ApiResponse<String>> deleteComment(@PathVariable Long commentId) {
                commentService.deleteComment(commentId);
                return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", null));
        }
}
