package petTopia.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.user.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByVerificationToken(String token);

    User findByEmailAndUserRole(String email, User.UserRole userRole);

    List<User> findByUserRole(User.UserRole userRole);

    boolean existsByEmail(String email);

    // TODO: Change to Optional<User>
    List<User> findByEmailAndProviderAndUserRole(String email, User.Provider provider, User.UserRole userRole);

    Optional<User> findByEmailIgnoreCaseAndUserRole(String email, User.UserRole userRole);
}
