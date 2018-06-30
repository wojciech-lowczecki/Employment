package pl.plh.app.employment.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.plh.app.employment.domain.Person;
import pl.plh.app.employment.domain.PersonDto;
import pl.plh.app.employment.domain.PersonToCreateDto;
import pl.plh.app.employment.domain.PersonToUpdateDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class PersonMapper {
    @Autowired
    private GenderMapper genderMapper;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private OccupationMapper occupationMapper;

    PersonMapper(GenderMapper genderMapper, LocationMapper locationMapper, OccupationMapper occupationMapper) {
        this.genderMapper = genderMapper;
        this.locationMapper = locationMapper;
        this.occupationMapper = occupationMapper;
    }

    public PersonDto mapToPersonDto(Person person) {
        return new PersonDto(
                person.getId(),
                person.getPesel(),
                person.getBirthDate(),
                genderMapper.mapToGenderDto(person.getGender()),
                person.getFirstName(),
                person.getLastName(),
                locationMapper.mapToLocationDto(person.getLocation()),
                occupationMapper.mapToOccupationDtoList(person.getOccupations())
        );
    }

    public List<PersonDto> mapToPersonDtoList(List<Person> personList) {
        return personList.stream()
                .map(this::mapToPersonDto)
                .collect(toList());
    }

    public Person mapToPerson(PersonDto personDto) {
        return new Person(
                personDto.getId(),
                personDto.getPesel(),
                personDto.getBirthDate(),
                genderMapper.mapToGender(personDto.getGender()),
                personDto.getFirstName(),
                personDto.getLastName(),
                locationMapper.mapToLocation(personDto.getLocation()),
                occupationMapper.mapToOccupationList(personDto.getOccupations())
        );
    }

    public Person mapToPerson(PersonToCreateDto personToCreateDto) {
        return new Person(
                null,
                personToCreateDto.getPesel(),
                personToCreateDto.getBirthDate(),
                genderMapper.mapToGender(personToCreateDto.getGender()),
                personToCreateDto.getFirstName(),
                personToCreateDto.getLastName(),
                locationMapper.mapToLocation(personToCreateDto.getLocationId()),
                occupationMapper.mapToOccupationListFromLongList(personToCreateDto.getOccupationIds())
        );
    }

    public Person mapToPerson(PersonToUpdateDto personToUpdateDto) {
        return new Person(
                personToUpdateDto.getId(),
                personToUpdateDto.getPesel(),
                personToUpdateDto.getBirthDate(),
                genderMapper.mapToGender(personToUpdateDto.getGender()),
                personToUpdateDto.getFirstName(),
                personToUpdateDto.getLastName(),
                locationMapper.mapToLocation(personToUpdateDto.getLocationId()),
                occupationMapper.mapToOccupationListFromLongList(personToUpdateDto.getOccupationIds())
        );
    }
}
