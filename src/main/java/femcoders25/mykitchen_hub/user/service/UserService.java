package femcoders25.mykitchen_hub.user.service;

import femcoders25.mykitchen_hub.common.exception.ResourceNotFoundException;
import femcoders25.mykitchen_hub.email.EmailService;
import femcoders25.mykitchen_hub.email.UserEmailTemplates;
import femcoders25.mykitchen_hub.user.dto.UserMapper;
import femcoders25.mykitchen_hub.user.dto.UserRegistrationDto;
import femcoders25.mykitchen_hub.user.dto.UserResponseDto;
import femcoders25.mykitchen_hub.user.dto.UserUpdateDto;
import femcoders25.mykitchen_hub.user.entity.Role;
import femcoders25.mykitchen_hub.user.entity.User;
import femcoders25.mykitchen_hub.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public User createUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByUsername(registrationDto.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(registrationDto.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(registrationDto.username());
        user.setEmail(registrationDto.email());
        user.setPassword(passwordEncoder.encode(registrationDto.password()));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);
        log.info("Created new user: {}", savedUser.getUsername());

        sendWelcomeEmail(savedUser);

        return savedUser;
    }

    private void sendWelcomeEmail(User user) {
        try {
            String subject = "Welcome to MyKitchen Hub! 🎉";
            String plainText = UserEmailTemplates.getUserWelcomeEmailPlainText(user);
            String htmlContent = UserEmailTemplates.getUserWelcomeEmailHtml(user);

            emailService.sendUserWelcomeEmail(user.getEmail(), subject, plainText, htmlContent);
            log.info("Welcome email sent successfully to user: {}", user.getUsername());
        } catch (MessagingException e) {
            log.error("Failed to send welcome email to user: {}", user.getUsername(), e);
        } catch (Exception e) {
            log.error("Unexpected error while sending welcome email to user: {}", user.getUsername(), e);
        }
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateDto updateDto) {
        if (updateDto == null) {
            throw new IllegalArgumentException("Update data cannot be null");
        }
        if (!isCurrentUser(id) && !isCurrentUserAdmin()) {
            throw new AccessDeniedException("You can only update your own profile");
        }
        User user = getUserById(id);
        if (updateDto.username() != null && !updateDto.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(updateDto.username())) {
                throw new IllegalArgumentException("Username already exists");
            }
        }
        if (updateDto.email() != null && !updateDto.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDto.email())) {
                throw new IllegalArgumentException("Email already exists");
            }
        }
        userMapper.updateEntity(user, updateDto);
        if (updateDto.password() != null) {
            user.setPassword(passwordEncoder.encode(updateDto.password()));
        }
        try {
            User savedUser = userRepository.save(user);
            log.info("Successfully updated user: {} (ID: {})", savedUser.getUsername(), savedUser.getId());
            return userMapper.toResponse(savedUser);
        } catch (Exception e) {
            log.error("Failed to update user with ID: {}", id, e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!isCurrentUser(id) && !isCurrentUserAdmin()) {
            throw new AccessDeniedException("You can only delete your own profile");
        }

        User user = getUserById(id);
        int recipeCount = user.getRecipes() != null ? user.getRecipes().size() : 0;
        int shoppingListCount = user.getShoppingLists() != null ? user.getShoppingLists().size() : 0;

        try {
            userRepository.delete(user);
            log.info("Successfully deleted user: {} (ID: {}) with {} recipes and {} shopping lists",
                    user.getUsername(), id, recipeCount, shoppingListCount);
        } catch (Exception e) {
            log.error("Failed to delete user with ID: {}", id, e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean isCurrentUser(Long userId) {
        try {
            User currentUser = getCurrentUser();
            return currentUser.getId().equals(userId);
        } catch (Exception e) {
            return false;
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    private boolean isCurrentUserAdmin() {
        try {
            User currentUser = getCurrentUser();
            return Role.ADMIN.equals(currentUser.getRole());
        } catch (Exception e) {
            log.warn("Could not determine if current user is admin", e);
            return false;
        }
    }
}
