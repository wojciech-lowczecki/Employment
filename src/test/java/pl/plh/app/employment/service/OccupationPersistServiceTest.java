package pl.plh.app.employment.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;
import pl.plh.app.employment.domain.Group;
import pl.plh.app.employment.domain.Occupation;
import pl.plh.app.employment.repository.OccupationRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@ActiveProfiles(profiles = {"test"})
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("Duplicates")
public class OccupationPersistServiceTest {
    @InjectMocks
    private OccupationPersistService db;

    @Mock
    private GroupPersistService groupService;

    @Mock
    private OccupationRepository occupationRepo;

    @Test
    public void testGetAllOccupations() {
        // Given
        Group group = new Group(1L, "Group test name");
        List<Occupation> occupations = Arrays.asList(new Occupation(1L, group, "Occupation test name"));

        when(occupationRepo.findAllByOrderByName()).thenReturn(occupations);

        // When
        List<Occupation> result = db.getAllOccupations();

        // Then
        assertEquals(occupations, result);
    }

    @Test
    public void testGetOccupation() {
        // Given
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");

        when(occupationRepo.findById(1L)).thenReturn(Optional.of(occupation));

        // When
        Occupation result = db.getOccupation(1L);

        // Then
        assertEquals(occupation, result);
    }

    @Test
    public void testGetOccupationWhenNoSuchOccupation() {
        // Given
        when(occupationRepo.findById(1L)).thenReturn(Optional.empty());

        try {
            // When
            db.getOccupation(1L);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Occupation object with id=1 does not exist", exc.getMessage());
        }
    }

    @Test
    public void testSaveOccupation() {
        // Given
        Group groupById = new Group(1L, null);
        final Occupation newOccupation = new Occupation(null, groupById, "Occupation test name");
        Group group = new Group(1L,  "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");

        when(groupService.getGroup(1L)).thenReturn(group);
        when(occupationRepo.save(newOccupation)).thenAnswer(inv -> { newOccupation.setId(1L); return newOccupation; });

        // When
        Occupation result = db.saveOccupation(newOccupation);

        //Then
        assertEquals(occupation, result);
    }

    @Test
    public void testSaveOccupationWhenNoSuchGroup() {
        // Given
        Group groupById = new Group(1L, null);
        Occupation newOccupation = new Occupation(null, groupById, "Occupation test name");

        when(groupService.getGroup(1L)).thenThrow(new NoSuchObjectException(Group.class, 1L));

        try {
            // When
            db.saveOccupation(newOccupation);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Group object with id=1 does not exist", exc.getMessage());
            verify(occupationRepo, never()).save(any());
        }
    }

    @Test
    public void testUpdateOccupation() {
        // Given
        Group groupById = new Group(1L, null);
        Occupation occupationToUpdate = new Occupation(1L, groupById, "Occupation test name");
        Group group = new Group(1L,  "Group test name");
        Occupation occupationAfterUpdate = new Occupation(1L, group, "Occupation test name");

        when(groupService.getGroup(1L)).thenReturn(group);
        when(occupationRepo.existsById(1L)).thenReturn(true);
        when(occupationRepo.save(occupationToUpdate)).thenReturn(occupationToUpdate);

        // When
        Occupation result = db.updateOccupation(occupationToUpdate);

        // Then
        assertEquals(occupationAfterUpdate, result);
    }

    @Test
    public void testUpdateOccupationWhenNoSuchOccupation() {
        // Given
        Group groupById = new Group(1L, null);
        Occupation occupation = new Occupation(1L, groupById, "Occupation test name");

        when(occupationRepo.existsById(1L)).thenReturn(false);

        try {
            // When
            db.updateOccupation(occupation);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Occupation object with id=1 does not exist", exc.getMessage());
            verify(occupationRepo, never()).save(any());
        }
    }

    @Test
    public void testUpdateOccupationWhenNoSuchGroup() {
        // Given
        Group groupById = new Group(1L, null);
        Occupation occupation = new Occupation(1L, groupById, "Occupation test name");

        when(groupService.getGroup(1L)).thenThrow(new NoSuchObjectException(Group.class, 1L));
        when(occupationRepo.existsById(1L)).thenReturn(true);

        try {
            // When
            db.updateOccupation(occupation);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Group object with id=1 does not exist", exc.getMessage());
            verify(occupationRepo, never()).save(any());
        }
    }

    @Test
    public void testDeleteOccupation() {
        // Given
        when(occupationRepo.existsById(1L)).thenReturn(true);

        // When
        db.deleteOccupation(1L);

        // Then
        verify(occupationRepo, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteOccupationWhenNoSuchOccupation() {
        // Given
        when(occupationRepo.existsById(1L)).thenReturn(false);

        try {
            // When
            db.deleteOccupation(1L);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Occupation object with id=1 does not exist", exc.getMessage());
            verify(occupationRepo, never()).deleteById(anyLong());
        }
    }
}
