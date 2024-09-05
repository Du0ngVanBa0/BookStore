package duongvanbao.Book.Store.repository;

import duongvanbao.Book.Store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    void deleteById(String id);
}
