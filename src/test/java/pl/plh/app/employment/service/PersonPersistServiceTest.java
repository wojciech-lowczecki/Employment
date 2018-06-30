package pl.plh.app.employment.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import pl.plh.app.employment.domain.*;
import pl.plh.app.employment.repository.OccupationRepository;
import pl.plh.app.employment.repository.PersonRepository;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@ActiveProfiles(profiles = {"test"})
@SuppressWarnings("Duplicates")
public class PersonPersistServiceTest {
    @InjectMocks
    private PersonPersistService db;

    @Mock
    private LocationPersistService locationService;

    @Mock
    private OccupationRepository occupationRepo;

    @Mock
    private PersonRepository personRepo;

    @Before
    public void initAnnotatedMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllPersons() {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", 1000000L);
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        List<Occupation> occupations = Arrays.asList(occupation);
        Person person = new Person(1L, "12345678901", LocalDate.of(1990, 12, 31), Gender.FEMALE, "Firstname", "Lastname",
                                   location, occupations);
        List<Person> persons = Arrays.asList(person);

        when(personRepo.findAllByOrderByLastNameAscFirstNameAscBirthDate()).thenReturn(persons);

        // When
        List<Person> result = db.getAllPersons();

        // Then
        assertEquals(persons, result);
    }

    @Test
    public void testGetPerson() {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", 1000000L);
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        List<Occupation> occupations = Arrays.asList(occupation);
        Person person = new Person(1L, "12345678901", LocalDate.of(1990, 12, 31), Gender.FEMALE, "Firstname", "Lastname",
                                   location, occupations);

        when(personRepo.findById(1L)).thenReturn(Optional.of(person));

        // When
        Person result = db.getPerson(1L);

        // Then
        assertEquals(person, result);
    }

    @Test
    public void testGetPersonWhenNoSuchPerson() {
        // Given
        when(personRepo.findById(1L)).thenReturn(Optional.empty());

        try {
            // When
            db.getPerson(1L);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Person object with id=1 does not exist", exc.getMessage());
        }
    }

    @Test
    public void testSavePerson() {
        // Given
        Location locationById = new Location(1L, null, null, null);
        Occupation occupationById = new Occupation(1L, null, null);
        List<Occupation> occupationsById = new ArrayList<>(Arrays.asList(occupationById));
        final Person newPerson = new Person(null, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John", "Smith",
                                            locationById, occupationsById);

        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", 1000000L);
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        List<Occupation> occupations = new ArrayList<>(Arrays.asList(occupation));
        Person person = new Person(1L, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John", "Smith",
                                   location, occupations);

        when(locationService.getLocation(1L)).thenReturn(location);
        when(occupationRepo.findAllById(Arrays.asList(1L))).thenReturn(occupations);
        when(personRepo.save(newPerson)).thenAnswer(inv -> { newPerson.setId(1L); return newPerson; });

        // When
        Person result = db.savePerson(newPerson);

        //Then
        assertEquals(person, result);
    }

    @Test
    public void testSavePersonWhenNoSuchLocation() {
        // Given
        Location locationById = new Location(1L, null, null, null);
        Occupation occupationById = new Occupation(1L, null, null);
        List<Occupation> occupationsById = new ArrayList<>(Arrays.asList(occupationById));
        Person newPerson = new Person(null, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John", "Smith",
                                      locationById, occupationsById);

        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        List<Occupation> occupations = new ArrayList<>(Arrays.asList(occupation));

        when(locationService.getLocation(1L)).thenThrow(new NoSuchObjectException(Location.class, 1L));
        when(occupationRepo.findAllById(Arrays.asList(1L))).thenReturn(occupations);

        try {
            // When
            db.savePerson(newPerson);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Location object with id=1 does not exist", exc.getMessage());
            verify(personRepo, never()).save(any());
        }
    }

    @Test
    public void testSavePersonWhenNoSuchOccupation() {
        // Given
        Location locationById = new Location(1L, null, null, null);
        Occupation occupationById = new Occupation(1L, null, null);
        List<Occupation> occupationsById = new ArrayList<>(Arrays.asList(occupationById));
        Person newPerson = new Person(null, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John", "Smith",
                                      locationById, occupationsById);

        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", 1000000L);

        when(locationService.getLocation(1L)).thenReturn(location);
        when(occupationRepo.findAllById(Arrays.asList(1L))).thenReturn(Collections.emptyList());

        try {
            // When
            db.savePerson(newPerson);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Occupation object with id=1 does not exist", exc.getMessage());
            verify(personRepo, never()).save(any());
        }
    }

    @Test
    public void testUpdatePerson() {
        // Given
        Location locationById = new Location(1L, null, null, null);
        Occupation occupationById = new Occupation(1L, null, null);
        List<Occupation> occupationsById = new ArrayList<>(Arrays.asList(occupationById));
        Person personToUpdate = new Person(1L, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John", "Smith",
                                           locationById, occupationsById);

        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", 1000000L);
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        List<Occupation> occupations = new ArrayList<>(Arrays.asList(occupation));
        Person personAfterUpdate = new Person(1L, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John", "Smith", location, occupations);

        when(locationService.getLocation(1L)).thenReturn(location);
        when(occupationRepo.findAllById(Arrays.asList(1L))).thenReturn(occupations);
        when(personRepo.existsById(1L)).thenReturn(true);
        when(personRepo.save(personToUpdate)).thenReturn(personToUpdate);

        // When
        Person result = db.updatePerson(personToUpdate);

        //Then
        assertEquals(personAfterUpdate, result);
    }

    @Test
    public void testUpdatePersonWhenNoSuchPerson() {
        // Given
        Location locationById = new Location(1L, null, null, null);
        Occupation occupationById = new Occupation(1L, null, null);
        List<Occupation> occupationsById = new ArrayList<>(Arrays.asList(occupationById));
        Person person = new Person(1L, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John", "Smith",
                                   locationById, occupationsById);

        when(personRepo.existsById(1L)).thenReturn(false);

        try {
            // When
            db.updatePerson(person);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Person object with id=1 does not exist", exc.getMessage());
            verify(personRepo, never()).save(any());
        }
    }

    @Test
    public void testUpdatePersonWhenNoSuchLocation() {
        // Given
        Location locationById = new Location(1L, null, null, null);
        Occupation occupationById = new Occupation(1L, null, null);
        List<Occupation> occupationsById = new ArrayList<>(Arrays.asList(occupationById));
        Person person = new Person(1L, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John", "Smith",
                                   locationById, occupationsById);

        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        List<Occupation> occupations = new ArrayList<>(Arrays.asList(occupation));

        when(locationService.getLocation(1L)).thenThrow(new NoSuchObjectException(Location.class, 1L));
        when(occupationRepo.findAllById(Arrays.asList(1L))).thenReturn(occupations);
        when(personRepo.existsById(1L)).thenReturn(true);

        try {
            // When
            db.savePerson(person);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Location object with id=1 does not exist", exc.getMessage());
            verify(personRepo, never()).save(any());
        }
    }

    @Test
    public void testUpdatePersonWhenNoSuchOccupation() {
        // Given
        Location locationById = new Location(1L, null, null, null);
        Occupation occupationById = new Occupation(1L, null, null);
        List<Occupation> occupationsById = new ArrayList<>(Arrays.asList(occupationById));
        Person person = new Person(1L, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John", "Smith",
                                   locationById, occupationsById);

        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", 1000000L);

        when(locationService.getLocation(1L)).thenReturn(location);
        when(occupationRepo.findAllById(Arrays.asList(1L))).thenReturn(Collections.emptyList());
        when(personRepo.existsById(1L)).thenReturn(true);

        try {
            // When
            db.savePerson(person);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Occupation object with id=1 does not exist", exc.getMessage());
            verify(personRepo, never()).save(any());
        }
    }

    @Test
    public void testDeletePerson() {
        // Given
        when(personRepo.existsById(1L)).thenReturn(true);

        // When
        db.deletePerson(1L);

        // Then
        verify(personRepo, times(1)).deleteById(1L);
    }

    @Test
    public void testDeletePersonWhenNoSuchPerson() {
        // Given
        when(personRepo.existsById(1L)).thenReturn(false);

        try {
            // When
            db.deletePerson(1L);
            // Then
            fail("Expected NoSuchObjectException to be thrown");
        } catch (NoSuchObjectException exc) {
            assertEquals("Person object with id=1 does not exist", exc.getMessage());
            verify(personRepo, never()).deleteById(anyLong());
        }
    }
}
