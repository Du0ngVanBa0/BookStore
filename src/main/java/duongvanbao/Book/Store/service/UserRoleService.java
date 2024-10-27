package duongvanbao.Book.Store.service;

import duongvanbao.Book.Store.model.UserRole;
import duongvanbao.Book.Store.repository.IUserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleService {
    @Autowired
    private final IUserRoleRepository userRoleRepository;

    public UserRoleService(IUserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public List<UserRole> findAll() {
        return userRoleRepository.findAll();
    }

    public void save (UserRole userRole) {
        userRoleRepository.save(userRole);
    }
}
