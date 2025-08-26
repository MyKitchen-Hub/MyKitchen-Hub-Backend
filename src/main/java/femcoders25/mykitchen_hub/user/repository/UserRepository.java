package femcoders25.mykitchen_hub.user.repository;

import femcoders25.mykitchen_hub.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
