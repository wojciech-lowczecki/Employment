package pl.plh.app.employment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.plh.app.employment.domain.Voivodeship;
import pl.plh.app.employment.repository.VoivodeshipRepository;

import java.util.List;

import static pl.plh.app.employment.service.PersistServiceValidator.checkIdExists;

@Transactional
@Service
public class VoivodeshipPersistService {
    @Autowired
    private VoivodeshipRepository voivodeshipRepo;

    public List<Voivodeship> getAllVoivodeships() {
        return voivodeshipRepo.findAllByOrderByName();
    }

    public Voivodeship getVoivodeship(final Long id) {
        return voivodeshipRepo.findById(id).orElseThrow(() -> new NoSuchObjectException(Voivodeship.class, id));
    }

    // For updating updateVoivodeship() is a better choice
    public Voivodeship saveVoivodeship(final Voivodeship voivodeship) {
        return voivodeshipRepo.save(voivodeship);
    }

    public Voivodeship updateVoivodeship(final Voivodeship voivodeship) {
        checkIdExists(voivodeshipRepo, voivodeship.getId(), Voivodeship.class);
        return voivodeshipRepo.save(voivodeship);
    }

    public void deleteVoivodeship(final Long id) {
        checkIdExists(voivodeshipRepo, id, Voivodeship.class);
        voivodeshipRepo.deleteById(id);
    }
}
