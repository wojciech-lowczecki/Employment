package pl.plh.app.employment.mapper;

import org.junit.Before;
import org.junit.Test;
import pl.plh.app.employment.domain.Voivodeship;
import pl.plh.app.employment.domain.VoivodeshipDto;
import pl.plh.app.employment.domain.VoivodeshipToCreateDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class VoivodeshipMapperTest {
    private VoivodeshipMapper voivodeshipMapper;

    @Before
    public void initVoivodeshipMapper() {
        voivodeshipMapper = new VoivodeshipMapper();
    }

    @Test
    public void testMapToVoivodeshipDto() {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");

        /// When @ Then
        assertEquals(voivodeshipDto, voivodeshipMapper.mapToVoivodeshipDto(voivodeship));
    }

    @Test
    public void testMapToVoivodeshipDtoList() {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        List<Voivodeship> voivodeshipList = Arrays.asList(voivodeship);
        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");
        List<VoivodeshipDto> voivodeshipDtoList = Arrays.asList(voivodeshipDto);

        // When @ Then
        assertEquals(voivodeshipDtoList, voivodeshipMapper.mapToVoivodeshipDtoList(voivodeshipList));
    }

    @Test
    public void testMapToVoivodeshipDtoListWhenEmpty() {
        // Given
        List<Voivodeship> voivodeshipList = new ArrayList<>();
        List<VoivodeshipDto> voivodeshipDtoList = new ArrayList<>();

        // When @ Then
        assertEquals(voivodeshipDtoList, voivodeshipMapper.mapToVoivodeshipDtoList(voivodeshipList));
    }

    @Test
    public void testMapToVoivodeshipFromVoivodeshipDto() {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");

        /// When @ Then
        assertEquals(voivodeship, voivodeshipMapper.mapToVoivodeship(voivodeshipDto));
    }

    @Test
    public void testMapToVoivodeshipFromVoivodeshipToCreateDto() {
        // Given
        Voivodeship voivodeship = new Voivodeship(null, "Voivodeship test name");
        VoivodeshipToCreateDto voivodeshipToCreateDto = new VoivodeshipToCreateDto("Voivodeship test name");

        /// When @ Then
        assertEquals(voivodeship, voivodeshipMapper.mapToVoivodeship(voivodeshipToCreateDto));
    }

    @Test
    public void testMapToVoivodeshipFromLong() {
        // Given
        Long voivodeshipId = 1L;
        Voivodeship voivodeship = new Voivodeship(1L, null);

        /// When @ Then
        assertEquals(voivodeship, voivodeshipMapper.mapToVoivodeship(voivodeshipId));
    }
}