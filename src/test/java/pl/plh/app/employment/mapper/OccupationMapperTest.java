package pl.plh.app.employment.mapper;

import org.junit.Before;
import org.junit.Test;
import pl.plh.app.employment.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OccupationMapperTest {
    private OccupationMapper occupationMapper;

    @Before
    public void initOccupationMapper() {
        occupationMapper = new OccupationMapper(new GroupMapper());
    }

    @Test
    public void testMapToOccupationDto() {
        // Given
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        GroupDto groupDto = new GroupDto(1L, "Group test name");
        OccupationDto occupationDto = new OccupationDto(1L, groupDto, "Occupation test name");

        // When @ Then
        assertEquals(occupationDto, occupationMapper.mapToOccupationDto(occupation));
    }

    @Test
    public void testMapToOccupationDtoList() {
        // Given
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        List<Occupation> occupationList = Arrays.asList(occupation);
        GroupDto groupDto = new GroupDto(1L, "Group test name");
        OccupationDto occupationDto = new OccupationDto(1L, groupDto, "Occupation test name");
        List<OccupationDto> occupationDtoList = Arrays.asList(occupationDto);

        // When @ Then
        assertEquals(occupationDtoList, occupationMapper.mapToOccupationDtoList(occupationList));
    }

    @Test
    public void testMapToOccupationDtoListWhenEmpty() {
        // Given
        List<Occupation> occupationList = new ArrayList<>();
        List<OccupationDto> occupationDtoList = new ArrayList<>();

        // When @ Then
        assertEquals(occupationDtoList, occupationMapper.mapToOccupationDtoList(occupationList));
    }

    @Test
    public void testMapToOccupationFromOccupationDto() {
        // Given
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        GroupDto groupDto = new GroupDto(1L, "Group test name");
        OccupationDto occupationDto = new OccupationDto(1L, groupDto, "Occupation test name");

        // When @ Then
        assertEquals(occupation, occupationMapper.mapToOccupation(occupationDto));
    }

    @Test
    public void testMapToOccupationList() {
        // Given
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        List<Occupation> occupationList = Arrays.asList(occupation);
        GroupDto groupDto = new GroupDto(1L, "Group test name");
        OccupationDto occupationDto = new OccupationDto(1L, groupDto, "Occupation test name");
        List<OccupationDto> occupationDtoList = Arrays.asList(occupationDto);

        // When @ Then
        assertEquals(occupationList, occupationMapper.mapToOccupationList(occupationDtoList));
    }

    @Test
    public void testMapToOccupationFromOccupationToCreateDto() {
        // Given
        Group group = new Group(1L, null);
        Occupation occupation = new Occupation(null, group, "Occupation test name");
        OccupationToCreateDto occupationToCreateDto = new OccupationToCreateDto(1L, "Occupation test name");

        // When @ Then
        assertEquals(occupation, occupationMapper.mapToOccupation(occupationToCreateDto));
    }

    @Test
    public void testMapToOccupationFromOccupationToUpdateDto() {
        // Given
        Group group = new Group(1L, null);
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        OccupationToUpdateDto occupationToUpdateDto = new OccupationToUpdateDto(1L, 1L, "Occupation test name");

        // When @ Then
        assertEquals(occupation, occupationMapper.mapToOccupation(occupationToUpdateDto));
    }

    @Test
    public void testMapToOccupationFromIdDto() {
        // Given
        Occupation occupation = new Occupation(1L, null, null);

        // When @ Then
        assertEquals(occupation, occupationMapper.mapToOccupation(1L));
    }

    @Test
    public void testMapToOccupationListFromLongList() {
        // Given
        List<Long> longList = Arrays.asList(1L);
        Occupation occupation = new Occupation(1L, null, null);
        List<Occupation> occupationList = Arrays.asList(occupation);

        // When @ Then
        assertEquals(occupationList, occupationMapper.mapToOccupationListFromLongList(longList));
    }
}