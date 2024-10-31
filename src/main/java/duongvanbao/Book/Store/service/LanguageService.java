package duongvanbao.Book.Store.service;

import duongvanbao.Book.Store.model.Language;
import duongvanbao.Book.Store.repository.ILanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LanguageService {
    @Autowired
    private ILanguageRepository languageRepository;

    public LanguageService(ILanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public List<Language> getAllLanguages() {
        return (List<Language>) languageRepository.findAll();
    }

    public Optional<Language> getLanguageById(String id) {
        return languageRepository.findById(id);
    }

    public Language saveLanguage(Language language) {
        return languageRepository.save(language);
    }

    public void deleteLanguage(Language language) {
        languageRepository.delete(language);
    }
}
