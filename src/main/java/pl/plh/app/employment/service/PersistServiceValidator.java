package pl.plh.app.employment.service;

import org.springframework.data.repository.CrudRepository;

public class PersistServiceValidator {
    private PersistServiceValidator() {
    }

    static <E> void checkIdExists(CrudRepository<E, Long> repo, Long id, Class entityClass) {
        if (!repo.existsById(id)) {
            throw new NoSuchObjectException(entityClass, id);
        }
    }
}
