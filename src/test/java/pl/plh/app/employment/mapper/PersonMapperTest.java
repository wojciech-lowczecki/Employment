package pl.plh.app.employment.mapper;

import org.junit.Before;
import org.junit.Test;
import pl.plh.app.employment.domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PersonMapperTest {
    private PersonMapper personMapper;

    @Before
    public void initPersonMapper() {
        personMapper = new PersonMapper(new GenderMapper(),
                                        new LocationMapper(new VoivodeshipMapper()),
                                        new OccupationMapper(new GroupMapper()));
    }

    @Test
    public void testMapToPersonDto() {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", 100000L);
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        List<Occupation> occupationList = Arrays.asList(occupation);
        Person person = new Person(1L, "11111111111", LocalDate.of(1990, 12, 31),Gender.MALE, "John", "Smith", location,
                                   occupationList);

        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");
        LocationDto locationDto = new LocationDto(1L, voivodeshipDto, "Location test name", 100000L);
        GroupDto groupDto = new GroupDto(1L, "Group test name");
        OccupationDto occupationDto = new OccupationDto(1L, groupDto, "Occupation test name");
        List<OccupationDto> occupationDtoList = Arrays.asList(occupationDto);
        PersonDto personDto = new PersonDto(1L, "11111111111", LocalDate.of(1990, 12, 31), GenderDto.MALE, "John",
                                            "Smith", locationDto, occupationDtoList);

        // When @ Then
        assertEquals(personDto, personMapper.mapToPersonDto(person));
    }

    @Test
    public void testMapToPersonDtoList() {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", 100000L);
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        List<Occupation> occupationList = Arrays.asList(occupation);
        Person person = new Person(1L, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John", "Smith",
                                   location, occupationList);
        List<Person> personList = Arrays.asList(person);

        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");
        LocationDto locationDto = new LocationDto(1L, voivodeshipDto, "Location test name", 100000L);
        GroupDto groupDto = new GroupDto(1L, "Group test name");
        OccupationDto occupationDto = new OccupationDto(1L, groupDto, "Occupation test name");
        List<OccupationDto> occupationDtoList = Arrays.asList(occupationDto);
        PersonDto personDto = new PersonDto(1L, "11111111111", LocalDate.of(1990, 12, 31), GenderDto.MALE, "John",
                                            "Smith", locationDto, occupationDtoList);
        List<PersonDto> personDtoList = Arrays.asList(personDto);

        // When @ Then
        assertEquals(personDtoList, personMapper.mapToPersonDtoList(personList));
    }

    @Test
    public void testMapToPersonDtoListWhenEmpty() {
        // Given
        List<Person> personList = new ArrayList<>();
        List<PersonDto> personDtoList = new ArrayList<>();

        // When @ Then
        assertEquals(personDtoList, personMapper.mapToPersonDtoList(personList));
    }

    @Test
    public void testMapToPersonFromPersonDto() {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", 100000L);
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        List<Occupation> occupationList = Arrays.asList(occupation);
        Person person = new Person(1L, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John", "Smith",
                                   location, occupationList);

        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");
        LocationDto locationDto = new LocationDto(1L, voivodeshipDto, "Location test name", 100000L);
        GroupDto groupDto = new GroupDto(1L, "Group test name");
        OccupationDto occupationDto = new OccupationDto(1L, groupDto, "Occupation test name");
        List<OccupationDto> occupationDtoList = Arrays.asList(occupationDto);
        PersonDto personDto = new PersonDto(1L, "11111111111", LocalDate.of(1990, 12, 31), GenderDto.MALE, "John",
                                            "Smith", locationDto, occupationDtoList);

        // When @ Then
        assertEquals(person, personMapper.mapToPerson(personDto));
    }

    @Test
    public void testMapToPersonFromPersonToCreateDto() {
        // Given
        Location location = new Location(1L, null, null, null);
        Occupation occupation = new Occupation(1L, null, null);
        List<Occupation> occupationList = Arrays.asList(occupation);
        Person person = new Person(null, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John", "Smith",
                                   location, occupationList);

        //IdDto locationIdDto = new IdDto(1L);
        //IdDto occupationIdDto = new IdDto(1L);
        List<Long> idList = Arrays.asList(1L);
        PersonToCreateDto personToCreateDto =  new PersonToCreateDto("11111111111", LocalDate.of(1990, 12, 31),
                                                                     GenderDto.MALE, "John", "Smith", 1L, idList);

        // When @ Then
        assertEquals(person, personMapper.mapToPerson(personToCreateDto));
    }

    @Test
    public void testMapToPersonFromPersonToUpdateDto() {
        // Given
        Location location = new Location(1L, null, null, null);
        Occupation occupation = new Occupation(1L, null, null);
        List<Occupation> occupationList = Arrays.asList(occupation);
        Person person = new Person(1L, "11111111111", LocalDate.of(1990, 12, 31), Gender.MALE, "John", "Smith",
                                   location, occupationList);

        //IdDto locationIdDto = new IdDto(1L);
        //IdDto occupationIdDto = new IdDto(1L);
        List<Long> idList = Arrays.asList(1L);
        PersonToUpdateDto personToUpdateDto =  new PersonToUpdateDto(1L, "11111111111", LocalDate.of(1990, 12, 31),
                                                                     GenderDto.MALE, "John", "Smith", 1L, idList);

        // When @ Then
        assertEquals(person, personMapper.mapToPerson(personToUpdateDto));
    }
}