package pl.plh.app.employment.controller;

import com.google.gson.Gson;
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
import pl.plh.app.employment.mapper.LocationMapper;
import pl.plh.app.employment.mapper.QueryVariablesMapper;
import pl.plh.app.employment.service.LocationPersistService;
import pl.plh.app.employment.service.NoSuchObjectException;

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
@WebMvcTest(LocationController.class)
public class LocationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationPersistService db;

    @MockBean
    private LocationMapper locationMapper;

    @MockBean
    private QueryVariablesMapper queryVariablesMapper;

    @Test
    public void testGetLocations() throws Exception {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", Long.MAX_VALUE);
        List<Location> locationList = Arrays.asList(location);
        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");
        LocationDto locationDto = new LocationDto(1L, voivodeshipDto, "Location test name", Long.MAX_VALUE);
        List<LocationDto> locationDtoList = Arrays.asList(locationDto);

        when(db.getAllLocations()).thenReturn(locationList);
        when(locationMapper.mapToLocationDtoList(locationList)).thenReturn(locationDtoList);

        // When & Then
        mockMvc.perform(get("/v1/locations").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].voivodeship.id", is(1)))
                .andExpect(jsonPath("$[0].voivodeship.name", is("Voivodeship test name")))
                .andExpect(jsonPath("$[0].name", is("Location test name")))
                .andExpect(jsonPath("$[0].population", is(Long.MAX_VALUE)));
    }

    @Test
    public void testGetLocationsEmptyResult() throws Exception {
        // Given
        List<Location> locationList = new ArrayList<>();
        List<LocationDto> locationDtoList = new ArrayList<>();

        when(db.getAllLocations()).thenReturn(locationList);
        when(locationMapper.mapToLocationDtoList(locationList)).thenReturn(locationDtoList);

        // When & Then
        mockMvc.perform(get("/v1/locations").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetLocation() throws Exception {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", Long.MAX_VALUE);
        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");
        LocationDto locationDto = new LocationDto(1L, voivodeshipDto, "Location test name", Long.MAX_VALUE);
        PositiveLongInQueryDto locationIdDto = new PositiveLongInQueryDto(1L);

        when(queryVariablesMapper.mapToLong(locationIdDto)).thenReturn(1L);
        when(db.getLocation(1L)).thenReturn(location);
        when(locationMapper.mapToLocationDto(location)).thenReturn(locationDto);

        // When & Then
        mockMvc.perform(get("/v1/locations/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.voivodeship.id", is(1)))
                .andExpect(jsonPath("$.voivodeship.name", is("Voivodeship test name")))
                .andExpect(jsonPath("$.name", is("Location test name")))
                .andExpect(jsonPath("$.population", is(Long.MAX_VALUE)));
    }

    @Test
    public void testGetLocationWhenNoSuchLocation() throws Exception {
        // Given
        PositiveLongInQueryDto locationIdDto = new PositiveLongInQueryDto(1L);

        when(queryVariablesMapper.mapToLong(locationIdDto)).thenReturn(1L);
        when(db.getLocation(1L)).thenThrow(new NoSuchObjectException(Location.class, 1L));

        // When & Then
        String path = "/v1/locations/1";
        mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Location object with id=1 does not exist")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testGetLocationWhenNonpositiveId() throws Exception {
        // Given & When & Then
        String path = "/v1/locations/-1";
        mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("locationId: value must be positive")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testCreateLocation() throws Exception {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", Long.MAX_VALUE);
        LocationToCreateDto locationToCreateDto = new LocationToCreateDto(1L, "Location test name", Long.MAX_VALUE);
        Voivodeship voivodeshipNotPersisted = new Voivodeship(1L, null);
        Location locationNotPersisted = new Location(null, voivodeshipNotPersisted, "Location test name", Long.MAX_VALUE);
        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");
        LocationDto locationDto = new LocationDto(1L, voivodeshipDto, "Location test name", Long.MAX_VALUE);

        when(locationMapper.mapToLocation(locationToCreateDto)).thenReturn(locationNotPersisted);
        when(db.saveLocation(locationNotPersisted)).thenReturn(location);
        when(locationMapper.mapToLocationDto(location)).thenReturn(locationDto);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(locationToCreateDto);

        // When & Then
        mockMvc.perform(post("/v1/locations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.voivodeship.id", is(1)))
                .andExpect(jsonPath("$.voivodeship.name", is("Voivodeship test name")))
                .andExpect(jsonPath("$.name", is("Location test name")))
                .andExpect(jsonPath("$.population", is(Long.MAX_VALUE)));
    }

    @Test
    public void testCreateLocationWhenNoSuchVoivodeship() throws Exception {
        // Given
        LocationToCreateDto locationToCreateDto = new LocationToCreateDto(1L, "Location test name", Long.MAX_VALUE);
        Voivodeship voivodeshipNotPersisted = new Voivodeship(1L, null);
        Location locationNotPersisted = new Location(null, voivodeshipNotPersisted, "Location test name", Long.MAX_VALUE);

        when(locationMapper.mapToLocation(locationToCreateDto)).thenReturn(locationNotPersisted);
        when(db.saveLocation(locationNotPersisted)).thenThrow(new NoSuchObjectException(Voivodeship.class, 1L));

        Gson gson = new Gson();
        String jsonContent = gson.toJson(locationToCreateDto);

        // When & Then
        String path = "/v1/locations";
        mockMvc.perform(post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Voivodeship object with id=1 does not exist")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testCreateLocationWhenNullVoivodeshipAndNameAndPopulation() throws Exception {
        // Given
        LocationToCreateDto locationToCreateDto = new LocationToCreateDto(null, null, null);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(locationToCreateDto);

        // When & Then
        String path = "/v1/locations";
        mockMvc.perform(post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Not valid fields")))
                .andExpect(jsonPath("$.details", containsInAnyOrder("voivodeshipId: must not be null",
                                                                    "name: must not be null",
                                                                    "population: must not be null")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testCreateLocationWhenTooShortNameAndNonpositiveVoivodeshipIdAndNonpositivePopulation() throws Exception {
        // Given
        LocationToCreateDto locationToCreateDto = new LocationToCreateDto(-1L, "A", -1L);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(locationToCreateDto);

        // When & Then
        String path = "/v1/locations";
        mockMvc.perform(post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Not valid fields")))
                .andExpect(jsonPath("$.details", containsInAnyOrder("voivodeshipId: must be greater than 0",
                                                                    "name: size must be between 2 and 255",
                                                                    "population: must be greater than 0")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testCreateLocationWhenDataIntegrityViolation() throws Exception {
        // Given
        LocationToCreateDto locationToCreateDto = new LocationToCreateDto(1L, "Location test name", Long.MAX_VALUE);
        Voivodeship voivodeshipNotPersisted = new Voivodeship(1L, null);
        Location locationNotPersisted = new Location(null, voivodeshipNotPersisted, "Location test name", Long.MAX_VALUE);

        when(locationMapper.mapToLocation(locationToCreateDto)).thenReturn(locationNotPersisted);
        String causeMessage = "Cause test message";
        when(db.saveLocation(locationNotPersisted))
                .thenThrow(new DataIntegrityViolationException("Test message", new RuntimeException(causeMessage)));

        Gson gson = new Gson();
        String jsonContent = gson.toJson(locationToCreateDto);

        // When & Then
        String path = "/v1/locations";
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
    public void testUpdateLocation() throws Exception {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        Location location = new Location(1L, voivodeship, "Location test name", Long.MAX_VALUE);
        LocationToUpdateDto locationToUpdateDto = new LocationToUpdateDto(1L, 1L, "Location test name", Long.MAX_VALUE);
        Voivodeship voivodeshipNotPersisted = new Voivodeship(1L, null);
        Location locationNotPersisted = new Location(1L, voivodeshipNotPersisted, "Location test name", Long.MAX_VALUE);
        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");
        LocationDto locationDto = new LocationDto(1L, voivodeshipDto, "Location test name", Long.MAX_VALUE);

        when(locationMapper.mapToLocation(locationToUpdateDto)).thenReturn(locationNotPersisted);
        when(db.updateLocation(locationNotPersisted)).thenReturn(location);
        when(locationMapper.mapToLocationDto(location)).thenReturn(locationDto);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(locationToUpdateDto);

        // When & Then
        mockMvc.perform(put("/v1/locations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.voivodeship.id", is(1)))
                .andExpect(jsonPath("$.voivodeship.name", is("Voivodeship test name")))
                .andExpect(jsonPath("$.name", is("Location test name")))
                .andExpect(jsonPath("$.population", is(Long.MAX_VALUE)));
    }

    @Test
    public void testUpdateLocationWhenNoSuchLocation() throws Exception {
        // Given
        LocationToUpdateDto locationToUpdateDto = new LocationToUpdateDto(1L, 1L, "Location test name", Long.MAX_VALUE);
        Voivodeship voivodeshipNotPersisted = new Voivodeship(1L, null);
        Location locationNotPersisted = new Location(1L, voivodeshipNotPersisted, "Location test name", Long.MAX_VALUE);

        when(locationMapper.mapToLocation(locationToUpdateDto)).thenReturn(locationNotPersisted);
        when(db.updateLocation(locationNotPersisted)).thenThrow(new NoSuchObjectException(Location.class, 1L));

        Gson gson = new Gson();
        String jsonContent = gson.toJson(locationToUpdateDto);

        // When & Then
        String path = "/v1/locations";
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
    public void testUpdateLocationWhenNoSuchVoivodeship() throws Exception {
        // Given
        LocationToUpdateDto locationToUpdateDto = new LocationToUpdateDto(1L, 1L, "Location test name", Long.MAX_VALUE);
        Voivodeship voivodeshipNotPersisted = new Voivodeship(1L, null);
        Location locationNotPersisted = new Location(1L, voivodeshipNotPersisted, "Location test name", Long.MAX_VALUE);

        when(locationMapper.mapToLocation(locationToUpdateDto)).thenReturn(locationNotPersisted);
        when(db.updateLocation(locationNotPersisted)).thenThrow(new NoSuchObjectException(Voivodeship.class, 1L));

        Gson gson = new Gson();
        String jsonContent = gson.toJson(locationToUpdateDto);

        // When & Then
        String path = "/v1/locations";
        mockMvc.perform(put(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Voivodeship object with id=1 does not exist")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testUpdateLocationWhenNullIdAndNullNameAndNullPopulation() throws Exception {
        // Given
        LocationToUpdateDto locationToUpdateDto = new LocationToUpdateDto(null, null, null, null);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(locationToUpdateDto);

        // When & Then
        String path = "/v1/locations";
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
                                                                    "voivodeshipId: must not be null",
                                                                    "name: must not be null",
                                                                    "population: must not be null")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testUpdateLocationWhenTooShortNameAndNonpositiveIdAndNonpositiveVoivodeshipIdAndNonpositivePopulation()
            throws Exception {
        // Given
        LocationToUpdateDto locationToUpdateDto = new LocationToUpdateDto(-1L, -1L, "A", -1L);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(locationToUpdateDto);

        // When & Then
        String path = "/v1/locations";
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
                                                                    "voivodeshipId: must be greater than 0",
                                                                    "name: size must be between 2 and 255",
                                                                    "population: must be greater than 0")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testUpdateLocationWhenDataIntegrityViolation() throws Exception {
        // Given
        LocationToUpdateDto locationToUpdateDto = new LocationToUpdateDto(1L, 1L, "Location test name", Long.MAX_VALUE);
        Voivodeship voivodeshipNotPersisted = new Voivodeship(1L, null);
        Location locationNotPersisted = new Location(1L, voivodeshipNotPersisted, "Location test name", Long.MAX_VALUE);

        when(locationMapper.mapToLocation(locationToUpdateDto)).thenReturn(locationNotPersisted);
        String causeMessage = "Cause test message";
        when(db.updateLocation(locationNotPersisted))
                .thenThrow(new DataIntegrityViolationException("Test message", new RuntimeException(causeMessage)));

        Gson gson = new Gson();
        String jsonContent = gson.toJson(locationToUpdateDto);

        // When & Then
        String path = "/v1/locations";
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
    public void testDeleteLocation() throws Exception {
        // Given
        PositiveLongInQueryDto idDto = new PositiveLongInQueryDto(Long.MAX_VALUE);
        when(queryVariablesMapper.mapToLong(idDto)).thenReturn(Long.MAX_VALUE);

        // When & Then
        mockMvc.perform(delete("/v1/locations/" + Long.MAX_VALUE).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(db, times(1)).deleteLocation(Long.MAX_VALUE);
    }

    @Test
    public void testDeleteLocationWhenNoSuchLocation() throws Exception {
        // Given
        PositiveLongInQueryDto idDto = new PositiveLongInQueryDto(Long.MAX_VALUE);

        when(queryVariablesMapper.mapToLong(idDto)).thenReturn(Long.MAX_VALUE);
        doThrow(new NoSuchObjectException(Location.class, Long.MAX_VALUE)).when(db).deleteLocation(Long.MAX_VALUE);

        // When & Then
        String path = "/v1/locations/" + Long.MAX_VALUE;
        mockMvc.perform(delete(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Location object with id=" + Long.MAX_VALUE + " does not exist")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testDeleteLocationWhenDataIntegrityViolation() throws Exception {
        // Given
        PositiveLongInQueryDto idDto = new PositiveLongInQueryDto(Long.MAX_VALUE);

        when(queryVariablesMapper.mapToLong(idDto)).thenReturn(Long.MAX_VALUE);
        String causeMessage = "Cause test message";
        doThrow(new DataIntegrityViolationException("Test message", new RuntimeException(causeMessage)))
                .when(db).deleteLocation(Long.MAX_VALUE);

        // When & Then
        String path = "/v1/locations/" + Long.MAX_VALUE;
        mockMvc.perform(delete(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is(causeMessage)))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testDeleteLocationWhenNonpositiveId() throws Exception {
        // Given & When & Then
        String path = "/v1/locations/-1";
        mockMvc.perform(delete(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("locationId: value must be positive")))
                .andExpect(jsonPath("$.path", is(path)));
    }
}