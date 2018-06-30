package pl.plh.app.employment.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;
import pl.plh.app.employment.domain.Voivodeship;
import pl.plh.app.employment.repository.VoivodeshipRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@ActiveProfiles(profiles = {"test"})
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("Duplicates")
public class VoivodeshipPersistServiceTest {
    @InjectMocks
    private VoivodeshipPersistService db;

    @Mock
    private VoivodeshipRepository voivodeshipRepo;

    @Test
    public void testGetAllVoivodeships() {
        // Given
        List<Voivodeship> voivodeships = Arrays.asList(new Voivodeship(1L, "Test Name"));
        when(voivodeshipRepo.findAllByOrderByName()).thenReturn(voivodeships);

        // When
        List<Voivodeship> result = db.getAllVoivodeships();

        // Then
        assertEquals(voivodeships, result);
    }

    @Test
    public void testGetVoivodeship() {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        when(voivodeshipRepo.findById(1L)).thenReturn(Optional.of(voivodeship));

        // When
        Voivodeship result = db.getVoivodeship(1L);

        // Then
        assertEquals(voivodeship, result);
    }

    @Test
    public void testGetVoivodeshipWhenNoSuchVoivodeship() {
        // Given
        when(voivodeshipRepo.findById(1L)).thenReturn(Optional.empty());

        try {
            // When
            db.getVoivodeship(1L);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Voivodeship object with id=1 does not exist", exc.getMessage());
        }
    }

    @Test
    public void testSaveVoivodeship() {
        // Given
        Voivodeship newVoivodeship = new Voivodeship(null, "Voivodeship test name");
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");

        when(voivodeshipRepo.save(newVoivodeship)).thenReturn(voivodeship);

        // When
        Voivodeship result = db.saveVoivodeship(newVoivodeship);

        //Then
        assertEquals(voivodeship, result);
    }

    @Test
    public void testUpdateVoivodeship() {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");

        when(voivodeshipRepo.existsById(1L)).thenReturn(true);
        when(voivodeshipRepo.save(voivodeship)).thenReturn(voivodeship);

        // When
        Voivodeship result = db.updateVoivodeship(voivodeship);

        //Then
        assertEquals(voivodeship, result);
    }

    @Test
    public void testUpdateVoivodeshipNoSuchVoivodeship() {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        when(voivodeshipRepo.existsById(1L)).thenReturn(false);

        try {
            // When
            db.updateVoivodeship(voivodeship);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Voivodeship object with id=1 does not exist", exc.getMessage());
            verify(voivodeshipRepo, never()).save(any());
        }
    }


    @Test
    public void testDeleteVoivodeship() {
        // Given
        when(voivodeshipRepo.existsById(1L)).thenReturn(true);

        // When
        db.deleteVoivodeship(1L);

        // Then
        verify(voivodeshipRepo, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteVoivodeshipWhenNoSuchVoivodeship() {
        // Given
        when(voivodeshipRepo.existsById(1L)).thenReturn(false);

        try {
            // When
            db.deleteVoivodeship(1L);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Voivodeship object with id=1 does not exist", exc.getMessage());
            verify(voivodeshipRepo, never()).deleteById(anyLong());
        }
    }
}
