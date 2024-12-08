package duongvanbao.Book.Store.service;

import duongvanbao.Book.Store.dto.user.RoleName;
import duongvanbao.Book.Store.model.Role;
import duongvanbao.Book.Store.repository.IRoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RoleService {
    private final IRoleRepository roleRepository;

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Optional<Role> findByName(RoleName roleName) {
        return roleRepository.findByName(roleName);
    }

    public void save (Role role) {
        roleRepository.save(role);
    }
}
