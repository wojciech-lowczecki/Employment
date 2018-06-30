package pl.plh.app.employment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.plh.app.employment.domain.Location;
import pl.plh.app.employment.domain.Voivodeship;
import pl.plh.app.employment.repository.LocationRepository;

import java.util.List;

import static pl.plh.app.employment.service.PersistServiceValidator.checkIdExists;

@Transactional
@Service
public class LocationPersistService {
    @Autowired
    private LocationRepository locationRepo;

    @Autowired
    private VoivodeshipPersistService voivodeshipService;

    public List<Location> getAllLocations() {
        return locationRepo.findAllByOrderByName();
    }

    public Location getLocation(final Long id) {
        return locationRepo.findById(id).orElseThrow(() -> new NoSuchObjectException(Location.class, id));
    }

    // Location.voivodeship member is used only for his id, the rest of Location.voivodeship fields are ignored
    // For updating updateLocation() is a better choice
    public Location saveLocation(final Location location) {
        location.setVoivodeship(refresh(location.getVoivodeship()));
        return locationRepo.save(location);
    }

    // Location.voivodeship member is used only for his id, the rest of Location.voivodeship fields are ignored
    public Location updateLocation(final Location location) {
        checkIdExists(locationRepo, location.getId(), Location.class);
        return saveLocation(location);
    }

    public void deleteLocation(final Long id) {
        checkIdExists(locationRepo, id, Location.class);
        locationRepo.deleteById(id);
    }

    private Voivodeship refresh(Voivodeship voivodeship) {
        return voivodeshipService.getVoivodeship(voivodeship.getId());
    }
}
