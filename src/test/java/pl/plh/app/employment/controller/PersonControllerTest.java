package pl.plh.app.employment.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.plh.app.employment.domain.*;
import pl.plh.app.employment.mapper.PersonMapper;
import pl.plh.app.employment.mapper.QueryVariablesMapper;
import pl.plh.app.employment.service.NoSuchObjectException;
import pl.plh.app.employment.service.PersonPersistService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(profiles = {"test"})
@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonPersistService db;

    @MockBean
    private PersonMapper personMapper;

    @MockBean
    private QueryVariablesMapper queryVariablesMapper;

    private static Gson gson;

    @BeforeClass
    public static void initGson() {
        gson = buildGson();
    }

    @Test
    public void testGetPersons() throws Exception {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", Long.MAX_VALUE);
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        List<Occupation> occupationList = Arrays.asList(occupation);
        Person person = new Person(1L, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John", "Smith",
                                   location, occupationList);
        List<Person> personList = Arrays.asList(person);

        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");
        LocationDto locationDto = new LocationDto(1L, voivodeshipDto, "Location test name", Long.MAX_VALUE);
        GroupDto groupDto = new GroupDto(1L, "Group test name");
        OccupationDto occupationDto = new OccupationDto(1L, groupDto, "Occupation test name");
        List<OccupationDto> occupationDtoList = Arrays.asList(occupationDto);
        PersonDto personDto = new PersonDto(1L, "11111111111", LocalDate.of(1990, 12, 31), GenderDto.MALE, "John",
                                            "Smith", locationDto, occupationDtoList);
        List<PersonDto> personDtoList = Arrays.asList(personDto);

        when(db.getAllPersons()).thenReturn(personList);
        when(personMapper.mapToPersonDtoList(personList)).thenReturn(personDtoList);

        // When & Then
        mockMvc.perform(get("/v1/persons").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].pesel", is("11111111111")))
                .andExpect(jsonPath("$[0].birthDate", is("1990-12-31")))
                .andExpect(jsonPath("$[0].gender", is("m")))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].lastName", is("Smith")))
                .andExpect(jsonPath("$[0].location.id", is(1)))
                .andExpect(jsonPath("$[0].location.name", is("Location test name")))
                .andExpect(jsonPath("$[0].location.population", is(Long.MAX_VALUE)))
                .andExpect(jsonPath("$[0].location.voivodeship.id", is(1)))
                .andExpect(jsonPath("$[0].location.voivodeship.name", is("Voivodeship test name")));
    }

    @Test
    public void testGetPersonsEmptyResult() throws Exception {
        // Given
        List<Person> personList = new ArrayList<>();
        List<PersonDto> personDtoList = new ArrayList<>();

        when(db.getAllPersons()).thenReturn(personList);
        when(personMapper.mapToPersonDtoList(personList)).thenReturn(personDtoList);

        // When & Then
        mockMvc.perform(get("/v1/persons").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetPerson() throws Exception {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", Long.MAX_VALUE);
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        List<Occupation> occupationList = Arrays.asList(occupation);
        Person person = new Person(1L, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John", "Smith",
                                   location, occupationList);

        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");
        LocationDto locationDto = new LocationDto(1L, voivodeshipDto, "Location test name", Long.MAX_VALUE);
        GroupDto groupDto = new GroupDto(1L, "Group test name");
        OccupationDto occupationDto = new OccupationDto(1L, groupDto, "Occupation test name");
        List<OccupationDto> occupationDtoList = Arrays.asList(occupationDto);
        PersonDto personDto = new PersonDto(1L, "11111111111", LocalDate.of(1990, 12, 31), GenderDto.MALE, "John",
                                            "Smith", locationDto, occupationDtoList);
        PositiveLongInQueryDto personIdDto = new PositiveLongInQueryDto(1L);

        when(queryVariablesMapper.mapToLong(personIdDto)).thenReturn(1L);
        when(db.getPerson(1L)).thenReturn(person);
        when(personMapper.mapToPersonDto(person)).thenReturn(personDto);

        // When & Then
        mockMvc.perform(get("/v1/persons/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.pesel", is("11111111111")))
                .andExpect(jsonPath("$.birthDate", is("1990-12-31")))
                .andExpect(jsonPath("$.gender", is("m")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Smith")))
                .andExpect(jsonPath("$.location.id", is(1)))
                .andExpect(jsonPath("$.location.name", is("Location test name")))
                .andExpect(jsonPath("$.location.population", is(Long.MAX_VALUE)))
                .andExpect(jsonPath("$.location.voivodeship.id", is(1)))
                .andExpect(jsonPath("$.location.voivodeship.name", is("Voivodeship test name")));
    }

    @Test
    public void testGetPersonWhenNoSuchPerson() throws Exception {
        // Given
        PositiveLongInQueryDto personIdDto = new PositiveLongInQueryDto(1L);

        when(queryVariablesMapper.mapToLong(personIdDto)).thenReturn(1L);
        when(db.getPerson(1L)).thenThrow(new NoSuchObjectException(Person.class, 1L));

        // When & Then
        String path = "/v1/persons/1";
        mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Person object with id=1 does not exist")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testGetPersonWhenNonpositiveId() throws Exception {
        // Given & When & Then
        String path = "/v1/persons/-1";
        mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("personId: value must be positive")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testCreatePerson() throws Exception {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", Long.MAX_VALUE);
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        List<Occupation> occupationList = Arrays.asList(occupation);
        Person person = new Person(1L, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John", "Smith",
                                   location, occupationList);

        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");
        LocationDto locationDto = new LocationDto(1L, voivodeshipDto, "Location test name", Long.MAX_VALUE);
        GroupDto groupDto = new GroupDto(1L, "Group test name");
        OccupationDto occupationDto = new OccupationDto(1L, groupDto, "Occupation test name");
        List<OccupationDto> occupationDtoList = Arrays.asList(occupationDto);
        PersonDto personDto = new PersonDto(1L, "11111111111", LocalDate.of(1990, 12, 31), GenderDto.MALE, "John",
                                            "Smith", locationDto, occupationDtoList);

        Location locationNotPersisted = new Location(1L, null, null, null);
        Occupation occupationNotPersisted = new Occupation(1L, null, null);
        List<Occupation> occupationNotPersistedList = Arrays.asList(occupationNotPersisted);
        Person personNotPersisted = new Person(null, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John",
                                               "Smith", locationNotPersisted, occupationNotPersistedList);

        List<Long> idList = Arrays.asList(1L);
        PersonToCreateDto personToCreateDto = new PersonToCreateDto("11111111111", LocalDate.of(1990, 12, 31),
                                                                    GenderDto.MALE, "John", "Smith", 1L, idList);

        when(personMapper.mapToPerson(personToCreateDto)).thenReturn(personNotPersisted);
        when(db.savePerson(personNotPersisted)).thenReturn(person);
        when(personMapper.mapToPersonDto(person)).thenReturn(personDto);

        String jsonContent = gson.toJson(personToCreateDto);

        // When & Then
        mockMvc.perform(post("/v1/persons")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.pesel", is("11111111111")))
                .andExpect(jsonPath("$.birthDate", is("1990-12-31")))
                .andExpect(jsonPath("$.gender", is("m")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Smith")))
                .andExpect(jsonPath("$.location.id", is(1)))
                .andExpect(jsonPath("$.location.name", is("Location test name")))
                .andExpect(jsonPath("$.location.population", is(Long.MAX_VALUE)))
                .andExpect(jsonPath("$.location.voivodeship.id", is(1)))
                .andExpect(jsonPath("$.location.voivodeship.name", is("Voivodeship test name")));
    }

    @Test
    public void testCreatePersonWhenNoSuchLocation() throws Exception {
        // Given
        Location locationNotPersisted = new Location(1L, null, null, null);
        Occupation occupationNotPersisted = new Occupation(1L, null, null);
        List<Occupation> occupationNotPersistedList = Arrays.asList(occupationNotPersisted);
        Person personNotPersisted = new Person(null, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John",
                                               "Smith", locationNotPersisted, occupationNotPersistedList);

        List<Long> idList = Arrays.asList(1L);
        PersonToCreateDto personToCreateDto = new PersonToCreateDto("11111111111", LocalDate.of(1990, 12, 31),
                                                                    GenderDto.MALE, "John", "Smith", 1L, idList);

        when(personMapper.mapToPerson(personToCreateDto)).thenReturn(personNotPersisted);
        when(db.savePerson(personNotPersisted)).thenThrow(new NoSuchObjectException(Location.class, 1L));

        String jsonContent = gson.toJson(personToCreateDto);

        // When & Then
        String path = "/v1/persons";
        mockMvc.perform(post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Location object with id=1 does not exist")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testCreatePersonWhenNoSuchOccupation() throws Exception {
        // Given
        Location locationNotPersisted = new Location(1L, null, null, null);
        Occupation occupationNotPersisted = new Occupation(1L, null, null);
        List<Occupation> occupationNotPersistedList = Arrays.asList(occupationNotPersisted);
        Person personNotPersisted = new Person(null, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John",
                                               "Smith", locationNotPersisted, occupationNotPersistedList);

        List<Long> idList = Arrays.asList(1L);
        PersonToCreateDto personToCreateDto = new PersonToCreateDto("11111111111", LocalDate.of(1990, 12, 31),
                                                                    GenderDto.MALE, "John", "Smith", 1L, idList);

        when(personMapper.mapToPerson(personToCreateDto)).thenReturn(personNotPersisted);
        when(db.savePerson(personNotPersisted)).thenThrow(new NoSuchObjectException(Occupation.class, 1L));

        String jsonContent = gson.toJson(personToCreateDto);

        // When & Then
        String path = "/v1/persons";
        mockMvc.perform(post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Occupation object with id=1 does not exist")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testCreatePersonWhenNullAllFields() throws Exception {
        // Given
        PersonToCreateDto personToCreateDto = new PersonToCreateDto(null, null, null, null, null, null, null);

        String jsonContent = gson.toJson(personToCreateDto);

        // When & Then
        String path = "/v1/persons";
        mockMvc.perform(post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Not valid fields")))
                .andExpect(jsonPath("$.details", containsInAnyOrder("pesel: must not be null",
                                                                    "birthDate: must not be null",
                                                                    "gender: must not be null",
                                                                    "firstName: must not be null",
                                                                    "lastName: must not be null",
                                                                    "locationId: must not be null",
                                                                    "occupationIds: must not be null")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testCreatePersonWhenTooShortPeselAndNamesAndEmptyOccupations() throws Exception {
        // Given
        List<Long> idList = Arrays.asList(1L);
        PersonToCreateDto personToCreateDto = new PersonToCreateDto("1111111111", LocalDate.of(1990, 12, 31),
                                                                    GenderDto.MALE, "J", "S", 1L, idList);

        String jsonContent = gson.toJson(personToCreateDto);

        // When & Then
        String path = "/v1/persons";
        mockMvc.perform(post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Not valid fields")))
                .andExpect(jsonPath("$.details", containsInAnyOrder("pesel: size must be between 11 and 11",
                                                                    "firstName: size must be between 2 and 255",
                                                                    "lastName: size must be between 2 and 255")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testCreatePersonWhenTooLongPeselAndTooLongOccupationsAndTooLateBirthDate() throws Exception {
        // Given
        List<Long> idList = Arrays.asList(1L, 2L, 3L, 4L);
        PersonToCreateDto personToCreateDto = new PersonToCreateDto("111111111112", LocalDate.now().plusDays(1),
                                                                    GenderDto.MALE, "John", "Smith", 1L, idList);

        String jsonContent = gson.toJson(personToCreateDto);

        // When & Then
        String path = "/v1/persons";
        mockMvc.perform(post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Not valid fields")))
                .andExpect(jsonPath("$.details", containsInAnyOrder("pesel: size must be between 11 and 11",
                                                                    "birthDate: must be a past date",
                                                                    "occupationIds: size must be between 0 and 3")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testCreatePersonWhenNonPositiveIdentifiers() throws Exception {
        // Given
        List<Long> idList = Arrays.asList(-1L);
        PersonToCreateDto personToCreateDto = new PersonToCreateDto("11111111111", LocalDate.of(1990, 12, 31),
                                                                    GenderDto.MALE, "John", "Smith", -1L, idList);

        String jsonContent = gson.toJson(personToCreateDto);

        // When & Then
        String path = "/v1/persons";
        mockMvc.perform(post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Not valid fields")))
                .andExpect(jsonPath("$.details", containsInAnyOrder( "locationId: must be greater than 0",
                                                                     "occupationIds[0]: must be greater than 0")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testCreatePersonWhenDataIntegrityViolation() throws Exception {
        // Given
        Location locationNotPersisted = new Location(1L, null, null, null);
        Occupation occupationNotPersisted = new Occupation(1L, null, null);
        List<Occupation> occupationNotPersistedList = Arrays.asList(occupationNotPersisted);
        Person personNotPersisted = new Person(null, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John",
                                               "Smith", locationNotPersisted, occupationNotPersistedList);

        List<Long> idList = Arrays.asList(1L);
        PersonToCreateDto personToCreateDto = new PersonToCreateDto("11111111111", LocalDate.of(1990, 12, 31),
                                                                    GenderDto.MALE, "John", "Smith", 1L, idList);

        when(personMapper.mapToPerson(personToCreateDto)).thenReturn(personNotPersisted);
        String causeMessage = "Cause test message";
        when(db.savePerson(personNotPersisted))
                .thenThrow(new DataIntegrityViolationException("Test message", new RuntimeException(causeMessage)));

        String jsonContent = gson.toJson(personToCreateDto);

        // When & Then
        String path = "/v1/persons";
        mockMvc.perform(post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is(causeMessage)))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testUpdatePerson() throws Exception {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", Long.MAX_VALUE);
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        List<Occupation> occupationList = Arrays.asList(occupation);
        Person person = new Person(1L, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John", "Smith",
                                   location, occupationList);

        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");
        LocationDto locationDto = new LocationDto(1L, voivodeshipDto, "Location test name", Long.MAX_VALUE);
        GroupDto groupDto = new GroupDto(1L, "Group test name");
        OccupationDto occupationDto = new OccupationDto(1L, groupDto, "Occupation test name");
        List<OccupationDto> occupationDtoList = Arrays.asList(occupationDto);
        PersonDto personDto = new PersonDto(1L, "11111111111", LocalDate.of(1990, 12, 31), GenderDto.MALE, "John",
                                            "Smith", locationDto, occupationDtoList);

        Location locationNotPersisted = new Location(1L, null, null, null);
        Occupation occupationNotPersisted = new Occupation(1L, null, null);
        List<Occupation> occupationNotPersistedList = Arrays.asList(occupationNotPersisted);
        Person personNotPersisted = new Person(1L, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John",
                                               "Smith", locationNotPersisted, occupationNotPersistedList);

        List<Long> idList = Arrays.asList(1L);
        PersonToUpdateDto personToUpdateDto = new PersonToUpdateDto(1L, "11111111111", LocalDate.of(1990, 12, 31),
                                                                    GenderDto.MALE, "John", "Smith", 1L, idList);

        when(personMapper.mapToPerson(personToUpdateDto)).thenReturn(personNotPersisted);
        when(db.updatePerson(personNotPersisted)).thenReturn(person);
        when(personMapper.mapToPersonDto(person)).thenReturn(personDto);

        String jsonContent = gson.toJson(personToUpdateDto);

        // When & Then
        mockMvc.perform(put("/v1/persons")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.pesel", is("11111111111")))
                .andExpect(jsonPath("$.birthDate", is("1990-12-31")))
                .andExpect(jsonPath("$.gender", is("m")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Smith")))
                .andExpect(jsonPath("$.location.id", is(1)))
                .andExpect(jsonPath("$.location.name", is("Location test name")))
                .andExpect(jsonPath("$.location.population", is(Long.MAX_VALUE)))
                .andExpect(jsonPath("$.location.voivodeship.id", is(1)))
                .andExpect(jsonPath("$.location.voivodeship.name", is("Voivodeship test name")));
    }

    @Test
    public void testUpdatePersonWhenNoSuchLocation() throws Exception {
        // Given
        Location locationNotPersisted = new Location(1L, null, null, null);
        Occupation occupationNotPersisted = new Occupation(1L, null, null);
        List<Occupation> occupationNotPersistedList = Arrays.asList(occupationNotPersisted);
        Person personNotPersisted = new Person(1L, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John",
                                               "Smith", locationNotPersisted, occupationNotPersistedList);

        List<Long> idList = Arrays.asList(1L);
        PersonToUpdateDto personToUpdateDto = new PersonToUpdateDto(1L, "11111111111", LocalDate.of(1990, 12, 31),
                                                                    GenderDto.MALE, "John", "Smith", 1L, idList);

        when(personMapper.mapToPerson(personToUpdateDto)).thenReturn(personNotPersisted);
        when(db.updatePerson(personNotPersisted)).thenThrow(new NoSuchObjectException(Location.class, 1L));

        String jsonContent = gson.toJson(personToUpdateDto);

        // When & Then
        String path = "/v1/persons";
        mockMvc.perform(put(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Location object with id=1 does not exist")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testUpdatePersonWhenNoSuchOccupation() throws Exception {
        // Given
        Location locationNotPersisted = new Location(1L, null, null, null);
        Occupation occupationNotPersisted = new Occupation(1L, null, null);
        List<Occupation> occupationNotPersistedList = Arrays.asList(occupationNotPersisted);
        Person personNotPersisted = new Person(1L, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John",
                                               "Smith", locationNotPersisted, occupationNotPersistedList);

        List<Long> idList = Arrays.asList(1L);
        PersonToUpdateDto personToUpdateDto = new PersonToUpdateDto(1L, "11111111111", LocalDate.of(1990, 12, 31),
                                                                    GenderDto.MALE, "John", "Smith", 1L, idList);

        when(personMapper.mapToPerson(personToUpdateDto)).thenReturn(personNotPersisted);
        when(db.updatePerson(personNotPersisted)).thenThrow(new NoSuchObjectException(Occupation.class, 1L));

        String jsonContent = gson.toJson(personToUpdateDto);

        // When & Then
        String path = "/v1/persons";
        mockMvc.perform(put(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Occupation object with id=1 does not exist")))
                .andExpect(jsonPath("$.path", is(path)));
    }


    @Test
    public void testUpdatePersonWhenNullAllFields() throws Exception {
        // Given
        PersonToUpdateDto personToUpdateDto = new PersonToUpdateDto(null, null, null, null, null, null, null, null);

        String jsonContent = gson.toJson(personToUpdateDto);

        // When & Then
        String path = "/v1/persons";
        mockMvc.perform(put(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                 .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Not valid fields")))
                .andExpect(jsonPath("$.details", containsInAnyOrder("id: must not be null",
                                                                    "pesel: must not be null",
                                                                    "birthDate: must not be null",
                                                                    "gender: must not be null",
                                                                    "firstName: must not be null",
                                                                    "lastName: must not be null",
                                                                    "locationId: must not be null",
                                                                    "occupationIds: must not be null")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testUpdatePersonWhenTooShortPeselAndNames() throws Exception {
        // Given
        List<Long> idList = Arrays.asList(1L);
        PersonToUpdateDto personToUpdateDto = new PersonToUpdateDto(1L, "1111111111", LocalDate.of(1990, 12, 31),
                                                                    GenderDto.MALE, "J", "S", 1L, idList);

        String jsonContent = gson.toJson(personToUpdateDto);

        // When & Then
        String path = "/v1/persons";
        mockMvc.perform(put(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Not valid fields")))
                .andExpect(jsonPath("$.details", containsInAnyOrder("pesel: size must be between 11 and 11",
                                                                    "firstName: size must be between 2 and 255",
                                                                    "lastName: size must be between 2 and 255")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testUpdatePersonWhenTooLongPeselAndTooLongOccupationsAndTooLateBirthDate() throws Exception {
        // Given
        List<Long> idList = Arrays.asList(1L, 2L, 3L, 4L);
        PersonToUpdateDto personToUpdateDto = new PersonToUpdateDto(1L, "111111111112", LocalDate.now().plusDays(1),
                                                                    GenderDto.MALE, "John", "Smith", 1L, idList);

        String jsonContent = gson.toJson(personToUpdateDto);

        // When & Then
        String path = "/v1/persons";
        mockMvc.perform(put(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Not valid fields")))
                .andExpect(jsonPath("$.details", containsInAnyOrder("pesel: size must be between 11 and 11",
                                                                    "birthDate: must be a past date",
                                                                    "occupationIds: size must be between 0 and 3")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testUpdatePersonWhenNonPositiveIdentifiers() throws Exception {
        // Given
        List<Long> idList = Arrays.asList(-1L);
        PersonToUpdateDto personToUpdateDto = new PersonToUpdateDto(-1L, "11111111111", LocalDate.of(1990, 12, 31),
                                                                    GenderDto.MALE, "John", "Smith", -1L, idList);

        String jsonContent = gson.toJson(personToUpdateDto);

        // When & Then
        String path = "/v1/persons";
        mockMvc.perform(put(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Not valid fields")))
                .andExpect(jsonPath("$.details", containsInAnyOrder("id: must be greater than 0",
                                                                    "locationId: must be greater than 0",
                                                                    "occupationIds[0]: must be greater than 0")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testUpdatePersonWhenDataIntegrityViolation() throws Exception {
        // Given
        Location locationNotPersisted = new Location(1L, null, null, null);
        Occupation occupationNotPersisted = new Occupation(1L, null, null);
        List<Occupation> occupationNotPersistedList = Arrays.asList(occupationNotPersisted);
        Person personNotPersisted = new Person(1L, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John",
                                               "Smith", locationNotPersisted, occupationNotPersistedList);

         List<Long> idList = Arrays.asList(1L);
        PersonToUpdateDto personToUpdateDto = new PersonToUpdateDto(1L, "11111111111", LocalDate.of(1990, 12, 31),
                                                                    GenderDto.MALE, "John", "Smith", 1L, idList);

        when(personMapper.mapToPerson(personToUpdateDto)).thenReturn(personNotPersisted);
        String causeMessage = "Cause test message";
        when(db.updatePerson(personNotPersisted))
                .thenThrow(new DataIntegrityViolationException("Test message", new RuntimeException(causeMessage)));

        String jsonContent = gson.toJson(personToUpdateDto);

        // When & Then
        String path = "/v1/persons";
        mockMvc.perform(put(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is(causeMessage)))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testDeletePerson() throws Exception {
        // Given
        PositiveLongInQueryDto idDto = new PositiveLongInQueryDto(Long.MAX_VALUE);
        when(queryVariablesMapper.mapToLong(idDto)).thenReturn(Long.MAX_VALUE);

        // When & Then
        mockMvc.perform(delete("/v1/persons/" + Long.MAX_VALUE).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(db, times(1)).deletePerson(Long.MAX_VALUE);
    }

    @Test
    public void testDeletePersonWhenNoSuchPerson() throws Exception {
        // Given
        PositiveLongInQueryDto idDto = new PositiveLongInQueryDto(Long.MAX_VALUE);
        when(queryVariablesMapper.mapToLong(idDto)).thenReturn(Long.MAX_VALUE);
        doThrow(new NoSuchObjectException(Person.class, Long.MAX_VALUE)).when(db).deletePerson(Long.MAX_VALUE);

        // When & Then
        String path = "/v1/persons/" + Long.MAX_VALUE;
        mockMvc.perform(delete(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Person object with id=" + Long.MAX_VALUE + " does not exist")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testDeletePersonWhenDataIntegrityViolation() throws Exception {
        // Given
        PositiveLongInQueryDto idDto = new PositiveLongInQueryDto(Long.MAX_VALUE);

        when(queryVariablesMapper.mapToLong(idDto)).thenReturn(Long.MAX_VALUE);
        String causeMessage = "Cause test message";
        doThrow(new DataIntegrityViolationException("Test message", new RuntimeException(causeMessage)))
                .when(db).deletePerson(Long.MAX_VALUE);

        // When & Then
        String path = "/v1/persons/" + Long.MAX_VALUE;
        mockMvc.perform(delete(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is(causeMessage)))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testDeletePersonWhenNonpositiveId() throws Exception {
        // Given & When & Then
        String path = "/v1/persons/-1";
        mockMvc.perform(delete(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("personId: value must be positive")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    private static Gson buildGson() {
        return new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (date, type, ctx)
                                     -> new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .create();
    }
}