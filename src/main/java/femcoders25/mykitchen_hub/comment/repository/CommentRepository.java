package femcoders25.mykitchen_hub.comment.repository;

import femcoders25.mykitchen_hub.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByRecipeIdOrderByCreatedAtDesc(Long recipeId);
}
