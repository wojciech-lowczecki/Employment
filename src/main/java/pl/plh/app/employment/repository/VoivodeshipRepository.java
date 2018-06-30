package pl.plh.app.employment.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.plh.app.employment.domain.Voivodeship;

import java.util.List;

@Transactional
@Repository
public interface VoivodeshipRepository extends CrudRepository<Voivodeship, Long> {
    List<Voivodeship> findAllByOrderByName();
}
