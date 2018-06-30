package pl.plh.app.employment.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;
import pl.plh.app.employment.domain.Location;
import pl.plh.app.employment.domain.Voivodeship;
import pl.plh.app.employment.repository.LocationRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@ActiveProfiles(profiles = {"test"})
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("Duplicates")
public class LocationPersistServiceTest {
    @InjectMocks
    private LocationPersistService db;

    @Mock
    private VoivodeshipPersistService voivodeshipService;

    @Mock
    private LocationRepository locationRepo;

    @Test
    public void testGetAllLocations() {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        List<Location> locations = Arrays.asList(new Location(1L, voivodeship, "Location test name", 1000000L));

        when(locationRepo.findAllByOrderByName()).thenReturn(locations);

        // When
        List<Location> result = db.getAllLocations();

        // Then
        assertEquals(locations, result);
    }

    @Test
    public void testGetLocation() {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", 1000000L);

        when(locationRepo.findById(1L)).thenReturn(Optional.of(location));

        // When
        Location result = db.getLocation(1L);

        // Then
        assertEquals(location, result);
    }

    @Test
    public void testGetLocationWhenNoSuchLocation() {
        // Given
        when(locationRepo.findById(1L)).thenReturn(Optional.empty());

        try {
            // When
            db.getLocation(1L);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Location object with id=1 does not exist", exc.getMessage());
        }
    }

    @Test
    public void testSaveLocation() {
        // Given
        Voivodeship voivodeshipById = new Voivodeship(1L, null);
        final Location newLocation  = new Location(null, voivodeshipById, "Location test name", 1000000L);
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location  = new Location(1L, voivodeship, "Location test name", 1000000L);

        when(voivodeshipService.getVoivodeship(1L)).thenReturn(voivodeship);
        when(locationRepo.save(newLocation)).thenAnswer(inv -> { newLocation.setId(1L); return newLocation; });

        // When
        Location result = db.saveLocation(newLocation);

        //Then
        assertEquals(location, result);
    }

    @Test
    public void testSaveLocationWhenNoSuchVoivodeship() {
        // Given
        Voivodeship voivodeshipById = new Voivodeship(1L, null);
        Location newLocation  = new Location(null, voivodeshipById, "Location test name", 1000000L);

        when(voivodeshipService.getVoivodeship(1L)).thenThrow(new NoSuchObjectException(Voivodeship.class, 1L));

        try {
            // When
            db.saveLocation(newLocation);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Voivodeship object with id=1 does not exist", exc.getMessage());
            verify(locationRepo, never()).save(any());
        }
    }

    @Test
    public void testUpdateLocation() {
        // Given
        Voivodeship voivodeshipById = new Voivodeship(1L, null);
        Location locationToUpdate = new Location(1L, voivodeshipById, "Location test name", 1000000L);
        Voivodeship voivodeship = new Voivodeship(1L,  "Voivodeship test name");
        Location locationAfterUpdate = new Location(1L, voivodeship, "Location test name", 1000000L);

        when(voivodeshipService.getVoivodeship(1L)).thenReturn(voivodeship);
        when(locationRepo.existsById(1L)).thenReturn(true);
        when(locationRepo.save(locationToUpdate)).thenReturn(locationToUpdate);

        // When
        Location result = db.updateLocation(locationToUpdate);

        //Then
        assertEquals(locationAfterUpdate, result);
    }

    @Test
    public void testUpdateLocationWhenNoSuchLocation() {
        // Given
        Voivodeship voivodeshipById = new Voivodeship(1L, null);
        Location location = new Location(1L, voivodeshipById, "Location test name", 1000000L);

        when(locationRepo.existsById(1L)).thenReturn(false);

        try {
            // When
            db.updateLocation(location);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Location object with id=1 does not exist", exc.getMessage());
            verify(locationRepo, never()).save(any());
        }
    }

    @Test
    public void testUpdateLocationNoSuchVoivodeship() {
        // Given
        Voivodeship voivodeshipById = new Voivodeship(1L, null);
        Location location = new Location(1L, voivodeshipById, "Location test name", 1000000L);

        when(voivodeshipService.getVoivodeship(1L)).thenThrow(new NoSuchObjectException(Voivodeship.class, 1L));
        when(locationRepo.existsById(1L)).thenReturn(true);

        try {
            // When
            db.updateLocation(location);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Voivodeship object with id=1 does not exist", exc.getMessage());
            verify(locationRepo, never()).save(any());
        }
    }

    @Test
    public void testDeleteLocation() {
        // Given
        when(locationRepo.existsById(1L)).thenReturn(true);

        // When
        db.deleteLocation(1L);

        // Then
        verify(locationRepo, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteLocationWhenNoSuchLocation() {
        // Given
        when(locationRepo.existsById(1L)).thenReturn(false);

        try {
            // When
            db.deleteLocation(1L);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Location object with id=1 does not exist", exc.getMessage());
            verify(locationRepo, never()).deleteById(anyLong());
        }
    }
}
