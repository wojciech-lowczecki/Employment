package pl.plh.app.employment.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.plh.app.employment.domain.Occupation;

import java.util.List;

@Transactional
@Repository
public interface OccupationRepository extends CrudRepository<Occupation, Long> {
    List<Occupation> findAllByOrderByName();

    @Override
    List<Occupation> findAllById(Iterable<Long> ids);
}
