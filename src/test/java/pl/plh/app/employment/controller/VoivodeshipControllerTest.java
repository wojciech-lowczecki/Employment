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
import pl.plh.app.employment.mapper.QueryVariablesMapper;
import pl.plh.app.employment.mapper.VoivodeshipMapper;
import pl.plh.app.employment.service.NoSuchObjectException;
import pl.plh.app.employment.service.VoivodeshipPersistService;

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
@WebMvcTest(VoivodeshipController.class)
public class VoivodeshipControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoivodeshipPersistService db;

    @MockBean
    private VoivodeshipMapper voivodeshipMapper;

    @MockBean
    private QueryVariablesMapper queryVariablesMapper;

    @Test
    public void testGetVoivodeships() throws Exception {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        List<Voivodeship> voivodeshipList = Arrays.asList(voivodeship);
        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");
        List<VoivodeshipDto> voivodeshipDtoList = Arrays.asList(voivodeshipDto);

        when(db.getAllVoivodeships()).thenReturn(voivodeshipList);
        when(voivodeshipMapper.mapToVoivodeshipDtoList(voivodeshipList)).thenReturn(voivodeshipDtoList);

        // When & Then
        mockMvc.perform(get("/v1/voivodeships").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Voivodeship test name")));
    }

    @Test
    public void testGetVoivodeshipsEmptyResult() throws Exception {
        // Given
        List<Voivodeship> voivodeshipList = new ArrayList<>();
        List<VoivodeshipDto> voivodeshipDtoList = new ArrayList<>();

        when(db.getAllVoivodeships()).thenReturn(voivodeshipList);
        when(voivodeshipMapper.mapToVoivodeshipDtoList(voivodeshipList)).thenReturn(voivodeshipDtoList);

        // When & Then
        mockMvc.perform(get("/v1/voivodeships").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetVoivodeship() throws Exception {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");
        PositiveLongInQueryDto voivodeshipIdDto = new PositiveLongInQueryDto(1L);

        when(queryVariablesMapper.mapToLong(voivodeshipIdDto)).thenReturn(1L);
        when(db.getVoivodeship(1L)).thenReturn(voivodeship);
        when(voivodeshipMapper.mapToVoivodeshipDto(voivodeship)).thenReturn(voivodeshipDto);

        // When & Then
        mockMvc.perform(get("/v1/voivodeships/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Voivodeship test name")));
    }

    @Test
    public void testGetVoivodeshipWhenNonpositiveId() throws Exception {
        // Given & When & Then
        String path = "/v1/voivodeships/-1";
        mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("voivodeshipId: value must be positive")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testGetVoivodeshipWhenNoSuchVoivodeship() throws Exception {
        // Given
        PositiveLongInQueryDto voivodeshipIdDto = new PositiveLongInQueryDto(1L);

        when(queryVariablesMapper.mapToLong(voivodeshipIdDto)).thenReturn(1L);
        when(db.getVoivodeship(1L)).thenThrow(new NoSuchObjectException(Voivodeship.class, 1L));

        // When & Then
        String path = "/v1/voivodeships/1";
        mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Voivodeship object with id=1 does not exist")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testCreateVoivodeship() throws Exception {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        VoivodeshipToCreateDto voivodeshipToCreateDto =  new VoivodeshipToCreateDto("Voivodeship test name");
        Voivodeship voivodeshipNotPersisted = new Voivodeship(null, "Voivodeship test name");
        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");

        when(voivodeshipMapper.mapToVoivodeship(voivodeshipToCreateDto)).thenReturn(voivodeshipNotPersisted);
        when(db.saveVoivodeship(voivodeshipNotPersisted)).thenReturn(voivodeship);
        when(voivodeshipMapper.mapToVoivodeshipDto(voivodeship)).thenReturn(voivodeshipDto);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(voivodeshipToCreateDto);

        // When & Then
        mockMvc.perform(post("/v1/voivodeships")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Voivodeship test name")));
    }

    @Test
    public void testCreateVoivodeshipWhenNullName() throws Exception {
        // Given
        VoivodeshipToCreateDto voivodeshipToCreateDto =  new VoivodeshipToCreateDto(null);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(voivodeshipToCreateDto);

        // When & Then
        String path = "/v1/voivodeships";
        mockMvc.perform(post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Not valid fields")))
                .andExpect(jsonPath("$.details", containsInAnyOrder("name: must not be null")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testCreateVoivodeshipWhenDataIntegrityViolation() throws Exception {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        VoivodeshipToCreateDto voivodeshipToCreateDto =  new VoivodeshipToCreateDto("Voivodeship test name");
        Voivodeship voivodeshipNotPersisted = new Voivodeship(null, "Voivodeship test name");
        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");

        when(voivodeshipMapper.mapToVoivodeship(voivodeshipToCreateDto)).thenReturn(voivodeshipNotPersisted);
        String causeMessage = "Cause test message";
        when(db.saveVoivodeship(voivodeshipNotPersisted))
                .thenThrow(new DataIntegrityViolationException("Test message", new RuntimeException(causeMessage)));

        Gson gson = new Gson();
        String jsonContent = gson.toJson(voivodeshipToCreateDto);

        // When & Then
        String path = "/v1/voivodeships";
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
    public void testCreateVoivodeshipWhenTooShortName() throws Exception {
        // Given
        VoivodeshipToCreateDto voivodeshipToCreateDto =  new VoivodeshipToCreateDto("A");

        Gson gson = new Gson();
        String jsonContent = gson.toJson(voivodeshipToCreateDto);

        // When & Then
        String path = "/v1/voivodeships";
        mockMvc.perform(post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Not valid fields")))
                .andExpect(jsonPath("$.details", containsInAnyOrder("name: size must be between 2 and 255")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testUpdateVoivodeship() throws Exception {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");

        when(voivodeshipMapper.mapToVoivodeship(voivodeshipDto)).thenReturn(voivodeship);
        when(db.updateVoivodeship(voivodeship)).thenReturn(voivodeship);
        when(voivodeshipMapper.mapToVoivodeshipDto(voivodeship)).thenReturn(voivodeshipDto);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(voivodeshipDto);

        // When & Then
        mockMvc.perform(put("/v1/voivodeships")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Voivodeship test name")));
    }

    @Test
    public void testUpdateVoivodeshipWhenTooShortNameAndNonpositiveId() throws Exception {
        // Given
        VoivodeshipDto voivodeshipDto =  new VoivodeshipDto(0L, "A");

        Gson gson = new Gson();
        String jsonContent = gson.toJson(voivodeshipDto);

        // When & Then
        String path = "/v1/voivodeships";
        mockMvc.perform(put(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Not valid fields")))
                .andExpect(jsonPath("$.details", containsInAnyOrder("name: size must be between 2 and 255",
                                                                    "id: must be greater than 0")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testUpdateVoivodeshipWhenNullNameAndNullId() throws Exception {
        // Given
        VoivodeshipDto voivodeshipDto =  new VoivodeshipDto(null, null);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(voivodeshipDto);

        // When & Then
        String path = "/v1/voivodeships";
        mockMvc.perform(put(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Not valid fields")))
                .andExpect(jsonPath("$.details", containsInAnyOrder("name: must not be null", "id: must not be null")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testUpdateVoivodeshipWhenNoSuchVoivodeship() throws Exception {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        VoivodeshipDto voivodeshipDto = new VoivodeshipDto(1L, "Voivodeship test name");

        when(voivodeshipMapper.mapToVoivodeship(voivodeshipDto)).thenReturn(voivodeship);
        when(db.updateVoivodeship(voivodeship)).thenThrow(new NoSuchObjectException(Voivodeship.class, 1L));

        Gson gson = new Gson();
        String jsonContent = gson.toJson(voivodeshipDto);

        // When & Then
        String path = "/v1/voivodeships";
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
    public void testUpdateVoivodeshipWhenDataIntegrityViolation() throws Exception {
        // Given
        Voivodeship voivodeship = new Voivodeship(1L, "Voivodeship test name");
        VoivodeshipDto voivodeshipDto =  new VoivodeshipDto(1L, "Voivodeship test name");

        when(voivodeshipMapper.mapToVoivodeship(voivodeshipDto)).thenReturn(voivodeship);
        String causeMessage = "Cause test message";
        when(db.updateVoivodeship(voivodeship))
                .thenThrow(new DataIntegrityViolationException("Test message", new RuntimeException(causeMessage)));

        Gson gson = new Gson();
        String jsonContent = gson.toJson(voivodeshipDto);

        // When & Then
        String path = "/v1/voivodeships";
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
    public void testDeleteVoivodeship() throws Exception {
        // Given
        PositiveLongInQueryDto idDto = new PositiveLongInQueryDto(Long.MAX_VALUE);
        when(queryVariablesMapper.mapToLong(idDto)).thenReturn(Long.MAX_VALUE);

        // When & Then
        mockMvc.perform(delete("/v1/voivodeships/" + Long.MAX_VALUE).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(db, times(1)).deleteVoivodeship(Long.MAX_VALUE);
    }

    @Test
    public void testDeleteVoivodeshipWhenDataIntegrityViolation() throws Exception {
        // Given
        PositiveLongInQueryDto idDto = new PositiveLongInQueryDto(Long.MAX_VALUE);

        when(queryVariablesMapper.mapToLong(idDto)).thenReturn(Long.MAX_VALUE);
        String causeMessage = "Cause test message";
        doThrow(new DataIntegrityViolationException("Test message", new RuntimeException(causeMessage)))
                .when(db).deleteVoivodeship(Long.MAX_VALUE);

        // When & Then
        String path = "/v1/voivodeships/" + Long.MAX_VALUE;
        mockMvc.perform(delete(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is(causeMessage)))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testDeleteVoivodeshipWhenNoSuchVoivodeship() throws Exception {
        // Given
        PositiveLongInQueryDto idDto = new PositiveLongInQueryDto(Long.MAX_VALUE);

        when(queryVariablesMapper.mapToLong(idDto)).thenReturn(Long.MAX_VALUE);
        doThrow(new NoSuchObjectException(Location.class, Long.MAX_VALUE)).when(db).deleteVoivodeship(Long.MAX_VALUE);

        // When & Then
        String path = "/v1/voivodeships/" + Long.MAX_VALUE;
        mockMvc.perform(delete(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Location object with id=" + Long.MAX_VALUE + " does not exist")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testDeleteVoivodeshipWhenNonpositiveId() throws Exception {
        // Given & When & Then
        String path = "/v1/voivodeships/-1";
        mockMvc.perform(delete(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("voivodeshipId: value must be positive")))
                .andExpect(jsonPath("$.path", is(path)));
    }
}