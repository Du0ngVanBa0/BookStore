package duongvanbao.Book.Store.repository;

import duongvanbao.Book.Store.dto.user.RoleName;
import duongvanbao.Book.Store.model.Role;

import java.util.Optional;

public interface IRoleRepository extends IBaseRepository<Role, String>{
    Optional<Role> findByName(RoleName name);
}
