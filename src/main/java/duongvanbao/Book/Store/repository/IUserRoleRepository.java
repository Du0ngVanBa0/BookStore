package duongvanbao.Book.Store.repository;

import duongvanbao.Book.Store.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRoleRepository extends JpaRepository<UserRole, String> {
}
