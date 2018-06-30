package pl.plh.app.employment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.plh.app.employment.domain.Group;
import pl.plh.app.employment.domain.Occupation;
import pl.plh.app.employment.repository.OccupationRepository;

import java.util.List;

import static pl.plh.app.employment.service.PersistServiceValidator.checkIdExists;

@Transactional
@Service
public class OccupationPersistService {
    @Autowired
    private OccupationRepository occupationRepo;

    @Autowired
    private GroupPersistService groupService;

    public List<Occupation> getAllOccupations() {
        return occupationRepo.findAllByOrderByName();
    }

    public Occupation getOccupation(final Long id) {
        return occupationRepo.findById(id).orElseThrow(() -> new NoSuchObjectException(Occupation.class, id));
    }

    // Occupation.group member is used only for his id, the rest of Occupation.group fields are ignored
    // For updating updateOccupation() is a better choice
    public Occupation saveOccupation(final Occupation occupation) {
        occupation.setGroup(refresh(occupation.getGroup()));
        return occupationRepo.save(occupation);
    }

    // Occupation.group member is used only for his id, the rest of Occupation.group fields are ignored
    public Occupation updateOccupation(final Occupation occupation) {
        checkIdExists(occupationRepo, occupation.getId(), Occupation.class);
        return saveOccupation(occupation);
    }

    public void deleteOccupation(final Long id) {
        checkIdExists(occupationRepo, id, Occupation.class);
        occupationRepo.deleteById(id);
    }

    private Group refresh(Group group) {
        return groupService.getGroup(group.getId());
    }
}
