package pl.plh.app.employment.mapper;

import org.springframework.stereotype.Component;
import pl.plh.app.employment.domain.Gender;
import pl.plh.app.employment.domain.GenderDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class GenderMapper {
    public GenderDto mapToGenderDto(Gender gender) {
        return GenderDto.valueOf(gender.name());
    }

    public List<GenderDto> mapToGenderDtoList(List<Gender> genderList) {
        return genderList.stream()
                .map(this::mapToGenderDto)
                .collect(toList());
    }

    public Gender mapToGender(GenderDto genderDto) {
        return Gender.valueOf(genderDto.name());
    }
}
