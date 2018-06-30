package pl.plh.app.employment.mapper;

import org.junit.Before;
import org.junit.Test;
import pl.plh.app.employment.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LocationMapperTest {
    private LocationMapper locationMapper;

    @Before
    public void initLocationMapper() {
        locationMapper = new LocationMapper(new VoivodeshipMapper());
    }

    @Test
    public void testMapToLocationDto() {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", 100000L);
        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");
        LocationDto locationDto = new LocationDto(1L, voivodeshipDto, "Location test name", 100000L);

        // When @ Then
        assertEquals(locationDto, locationMapper.mapToLocationDto(location));
    }

    @Test
    public void testMapToLocationDtoList() {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", 100000L);
        List<Location> locationList = Arrays.asList(location);
        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");
        LocationDto locationDto = new LocationDto(1L, voivodeshipDto, "Location test name", 100000L);
        List<LocationDto> locationDtoList = Arrays.asList(locationDto);

        // When @ Then
        assertEquals(locationDtoList, locationMapper.mapToLocationDtoList(locationList));
    }

    @Test
    public void testMapToLocationDtoListWhenEmpty() {
        // Given
        List<Location> locationList = new ArrayList<>();
        List<LocationDto> locationDtoList = new ArrayList<>();

        // When @ Then
        assertEquals(locationDtoList, locationMapper.mapToLocationDtoList(locationList));
    }

    @Test
    public void testMapToLocationFromLocationDto() {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", 100000L);
        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");
        LocationDto locationDto = new LocationDto(1L, voivodeshipDto, "Location test name", 100000L);

        // When @ Then
        assertEquals(location, locationMapper.mapToLocation(locationDto));
    }

    @Test
    public void testMapToLocationFromLocationToUpdateDto() {
        // Given
        LocationToUpdateDto locationToUpdateDto = new LocationToUpdateDto(1L, 1L, "Location test name", 100000L);
        Voivodeship voivodeship = new Voivodeship(1L, null);
        Location location = new Location(1L, voivodeship, "Location test name", 100000L);

        // When @ Then
        assertEquals(location, locationMapper.mapToLocation(locationToUpdateDto));
    }

    @Test
    public void testMapToLocationFromLocationToCreateDto() {
        // Given
        LocationToCreateDto locationToCreateDto = new LocationToCreateDto(1L, "Location test name", 100000L);
        Voivodeship voivodeship = new Voivodeship(1L, null);
        Location location = new Location(null, voivodeship, "Location test name", 100000L);

        // When @ Then
        assertEquals(location, locationMapper.mapToLocation(locationToCreateDto));
    }

    @Test
    public void testMapToLocationFromLong() {
        // Given
        Location location = new Location(1L, null, null, null);

        // When @ Then
        assertEquals(location, locationMapper.mapToLocation(1L));
    }
}