package pl.plh.app.employment.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;
import pl.plh.app.employment.domain.Group;
import pl.plh.app.employment.repository.GroupRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@ActiveProfiles(profiles = {"test"})
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("Duplicates")
public class GroupPersistServiceTest {
    @InjectMocks
    private GroupPersistService db;

    @Mock
    private GroupRepository groupRepo;

    @Test
    public void testGetAllGroups() {
        // Given
        List<Group> groups = Arrays.asList(new Group(1L, "Test Name"));
        when(groupRepo.findAllByOrderByName()).thenReturn(groups);

        // When
        List<Group> result = db.getAllGroups();

        // Then
        assertEquals(groups, result);
    }

    @Test
    public void testGetGroup() {
        // Given
        Group group = new Group(1L, "group test name");
        when(groupRepo.findById(1L)).thenReturn(Optional.of(group));

        // When
        Group result = db.getGroup(1L);

        // Then
        assertEquals(group, result);
    }

    @Test
    public void testGetGroupNoSuchGroup() {
        // Given
        when(groupRepo.findById(1L)).thenReturn(Optional.empty());

        try {
        // When
            db.getGroup(1L);
        // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Group object with id=1 does not exist", exc.getMessage());
        }
    }

    @Test
    public void testSaveGroup() {
        // Given
        Group newGroup = new Group(null, "Group test name");
        Group group = new Group(1L, "Group test name");

        when(groupRepo.save(newGroup)).thenReturn(group);

        // When
        Group result = db.saveGroup(newGroup);

        //Then
        assertEquals(group, result);
    }

    @Test
    public void testUpdateGroup() {
        // Given
        Group group = new Group(1L, "Group test name");

        when(groupRepo.existsById(1L)).thenReturn(true);
        when(groupRepo.save(group)).thenReturn(group);

        // When
        Group result = db.updateGroup(group);

        //Then
        assertEquals(group, result);
    }

    @Test
    public void testUpdateGroupWhenNoSuchGroup() {
        // Given
        Group group = new Group(1L, "Group test name");
        when(groupRepo.existsById(1L)).thenReturn(false);

        try {
            // When
            db.updateGroup(group);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Group object with id=1 does not exist", exc.getMessage());
            verify(groupRepo, never()).save(any());
        }
    }

    @Test
    public void testDeleteGroup() {
        // Given
        when(groupRepo.existsById(1L)).thenReturn(true);

        // When
        db.deleteGroup(1L);

        // Then
        verify(groupRepo, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteGroupWhenNoSuchGroup() {
        // Given
        when(groupRepo.existsById(1L)).thenReturn(false);

        try {
            // When
            db.deleteGroup(1L);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Group object with id=1 does not exist", exc.getMessage());
            verify(groupRepo, never()).deleteById(anyLong());
        }
    }
}
