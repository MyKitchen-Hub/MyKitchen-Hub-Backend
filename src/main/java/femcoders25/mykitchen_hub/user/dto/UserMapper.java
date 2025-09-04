package femcoders25.mykitchen_hub.user.dto;

import femcoders25.mykitchen_hub.user.entity.User;
import femcoders25.mykitchen_hub.user.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDto toResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    public User toEntity(UserRegistrationDto registrationDto) {
        if (registrationDto == null) {
            return null;
        }

        User user = new User();
        user.setUsername(registrationDto.username());
        user.setEmail(registrationDto.email());
        user.setRole(Role.USER);
        return user;
    }

    public void updateEntity(User user, UserUpdateDto updateDto) {
        if (user == null || updateDto == null) {
            return;
        }
        if (updateDto.username() != null) {
            user.setUsername(updateDto.username());
        }
        if (updateDto.email() != null) {
            user.setEmail(updateDto.email());
        }
    }
}
