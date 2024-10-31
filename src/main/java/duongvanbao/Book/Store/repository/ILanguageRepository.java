package duongvanbao.Book.Store.repository;

import duongvanbao.Book.Store.model.Language;
import org.springframework.data.repository.CrudRepository;

public interface ILanguageRepository extends CrudRepository<Language, String> {
}
