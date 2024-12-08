package duongvanbao.Book.Store.repository;

import duongvanbao.Book.Store.model.User;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface UserRepository extends IBaseRepository<User, String>{
    Optional<User> findByEmail(String email);
}
