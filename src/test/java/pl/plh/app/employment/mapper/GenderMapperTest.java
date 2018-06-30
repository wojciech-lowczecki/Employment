package pl.plh.app.employment.mapper;

import org.junit.Before;
import org.junit.Test;
import pl.plh.app.employment.domain.Gender;
import pl.plh.app.employment.domain.GenderDto;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class GenderMapperTest {
    private GenderMapper genderMapper;

    @Before
    public void initGenderMapper() {
        genderMapper = new GenderMapper();
    }

    @Test
    public void testMapToGenderDto() {
        // Given &When & Then
        assertSame(GenderDto.FEMALE, genderMapper.mapToGenderDto(Gender.FEMALE));
        assertSame(GenderDto.MALE, genderMapper.mapToGenderDto(Gender.MALE));
    }

    @Test
    public void testMapToGenderDtoList() {
        // Given
        Gender female = Gender.FEMALE;
        Gender male = Gender.MALE;
        List<Gender> genderList = Arrays.asList(male, female);
        GenderDto femaleDto = GenderDto.FEMALE;
        GenderDto maleDto = GenderDto.MALE;
        List<GenderDto> genderDtoList = Arrays.asList(maleDto, femaleDto);

        // When & Then
        assertEquals(genderDtoList, genderMapper.mapToGenderDtoList(genderList));
    }

    @Test
    public void testMapToGender() {
        // Given & When & Then
        assertSame(Gender.FEMALE, genderMapper.mapToGender(GenderDto.FEMALE));
        assertSame(Gender.MALE, genderMapper.mapToGender(GenderDto.MALE));
    }
}