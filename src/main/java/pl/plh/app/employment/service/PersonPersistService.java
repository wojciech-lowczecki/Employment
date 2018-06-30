package pl.plh.app.employment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.plh.app.employment.domain.Location;
import pl.plh.app.employment.domain.Occupation;
import pl.plh.app.employment.domain.Person;
import pl.plh.app.employment.repository.OccupationRepository;
import pl.plh.app.employment.repository.PersonRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static pl.plh.app.employment.service.PersistServiceValidator.checkIdExists;

@Transactional
@Service
public class PersonPersistService {
    @Autowired
    private LocationPersistService locationService;

    @Autowired
    private OccupationRepository occupationRepo;

    @Autowired
    private PersonRepository personRepo;

    public List<Person> getAllPersons() {
        return personRepo.findAllByOrderByLastNameAscFirstNameAscBirthDate();
    }

    public Person getPerson(final Long id) {
        return personRepo.findById(id).orElseThrow(() -> new NoSuchObjectException(Person.class, id));
    }

    // Person.location member is used only for his id, the rest of Person.location fields are ignored
    // Elements of List<Occupation> Person.occupations member are used only for their ids, the rest
    // of their fields are ignored
    // For updating updatePerson() is a better choice
    public Person savePerson(final Person person) {
        person.setLocation(refresh(person.getLocation()));
        person.setOccupations(refresh(person.getOccupations()));
        return personRepo.save(person);
    }

    // Person.location member is used only for his id, the rest of Person.location fields are ignored
    // Elements of List<Occupation> Person.occupations member are used only for their ids, the rest
    // of their fields are ignored
    public Person updatePerson(final Person person) {
        checkIdExists(personRepo, person.getId(), Person.class);
        return savePerson(person);
    }

    public void deletePerson(final Long id) {
        checkIdExists(personRepo, id, Person.class);
        personRepo.deleteById(id);
    }

    private Location refresh(Location location) {
        return locationService.getLocation(location.getId());
    }

    private List<Occupation> refresh(List<Occupation> occupations) {
        if (occupations.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> searchedIds = occupations.stream()
                .map(Occupation::getId)
                .collect(toList());
        List<Occupation> refreshed = occupationRepo.findAllById(searchedIds);

        List<Long> refreshedIds = refreshed.stream()
                .map(Occupation::getId)
                .collect(toList());
        if (searchedIds.size() > refreshedIds.size()) {
            for (long id : searchedIds) {
                if (!refreshedIds.contains(id)) {
                    throw new NoSuchObjectException(Occupation.class, id);
                }
            }
        }

        return refreshed;
    }
}
