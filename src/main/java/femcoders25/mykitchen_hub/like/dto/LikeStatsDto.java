package femcoders25.mykitchen_hub.like.dto;

public record LikeStatsDto(
        long likesCount,
        long dislikesCount,
        boolean userLiked,
        boolean userDisliked) {
}
