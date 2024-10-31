package duongvanbao.Book.Store.repository;

import duongvanbao.Book.Store.model.Book;
import org.springframework.data.repository.CrudRepository;

public interface IBookRepository extends CrudRepository<Book, String> {
}
