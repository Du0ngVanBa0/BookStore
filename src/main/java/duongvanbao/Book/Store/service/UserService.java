package duongvanbao.Book.Store.service;

import duongvanbao.Book.Store.model.User;
import duongvanbao.Book.Store.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void deleteById(String id) { this.userRepository.deleteById(id);}
}
