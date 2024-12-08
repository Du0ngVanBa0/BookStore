package duongvanbao.Book.Store.service;

import duongvanbao.Book.Store.model.UserRole;
import duongvanbao.Book.Store.repository.IUserRoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserRoleService {
    private final IUserRoleRepository userRoleRepository;

    public List<UserRole> findAll() {
        return userRoleRepository.findAll();
    }

    public void save (UserRole userRole) {
        userRoleRepository.save(userRole);
    }
}
