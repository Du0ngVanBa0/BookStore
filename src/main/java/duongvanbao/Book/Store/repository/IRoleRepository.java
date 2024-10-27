package duongvanbao.Book.Store.repository;

import duongvanbao.Book.Store.dto.user.RoleName;
import duongvanbao.Book.Store.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(RoleName name);
}
