package duongvanbao.Book.Store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IBaseRepository<T, ID> extends JpaRepository<T, ID> {
}
