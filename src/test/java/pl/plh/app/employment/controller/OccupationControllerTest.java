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
import pl.plh.app.employment.mapper.OccupationMapper;
import pl.plh.app.employment.mapper.QueryVariablesMapper;
import pl.plh.app.employment.service.NoSuchObjectException;
import pl.plh.app.employment.service.OccupationPersistService;

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
@WebMvcTest(OccupationController.class)
public class OccupationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OccupationPersistService db;

    @MockBean
    private OccupationMapper occupationMapper;

    @MockBean
    private QueryVariablesMapper queryVariablesMapper;

    @Test
    public void testGetOccupations() throws Exception {
        // Given
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        List<Occupation> occupationList = Arrays.asList(occupation);
        GroupDto groupDto = new GroupDto(1L, "Group test name");
        OccupationDto occupationDto = new OccupationDto(1L, groupDto, "Occupation test name");
        List<OccupationDto> occupationDtoList = Arrays.asList(occupationDto);

        when(db.getAllOccupations()).thenReturn(occupationList);
        when(occupationMapper.mapToOccupationDtoList(occupationList)).thenReturn(occupationDtoList);

        // When & Then
        mockMvc.perform(get("/v1/occupations").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].group.id", is(1)))
                .andExpect(jsonPath("$[0].group.name", is("Group test name")))
                .andExpect(jsonPath("$[0].name", is("Occupation test name")));
    }

    @Test
    public void testGetOccupationsEmptyResult() throws Exception {
        // Given
        List<Occupation> occupationList = new ArrayList<>();
        List<OccupationDto> occupationDtoList = new ArrayList<>();

        when(db.getAllOccupations()).thenReturn(occupationList);
        when(occupationMapper.mapToOccupationDtoList(occupationList)).thenReturn(occupationDtoList);

        // When & Then
        mockMvc.perform(get("/v1/occupations").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetOccupation() throws Exception {
        // Given
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        GroupDto groupDto = new GroupDto(1L, "Group test name");
        OccupationDto occupationDto = new OccupationDto(1L, groupDto, "Occupation test name");
        PositiveLongInQueryDto occupationIdDto = new PositiveLongInQueryDto(1L);

        when(queryVariablesMapper.mapToLong(occupationIdDto)).thenReturn(1L);
        when(db.getOccupation(1L)).thenReturn(occupation);
        when(occupationMapper.mapToOccupationDto(occupation)).thenReturn(occupationDto);

        // When & Then
        mockMvc.perform(get("/v1/occupations/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.group.id", is(1)))
                .andExpect(jsonPath("$.group.name", is("Group test name")))
                .andExpect(jsonPath("$.name", is("Occupation test name")));
    }

    @Test
    public void testGetOccupationWhenNoSuchOccupation() throws Exception {
        // Given
        PositiveLongInQueryDto occupationIdDto = new PositiveLongInQueryDto(1L);

        when(queryVariablesMapper.mapToLong(occupationIdDto)).thenReturn(1L);
        when(db.getOccupation(1L)).thenThrow(new NoSuchObjectException(Occupation.class, 1L));

        // When & Then
        String path = "/v1/occupations/1";
        mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Occupation object with id=1 does not exist")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testGetOccupationWhenNonpositiveId() throws Exception {
        // Given & When & Then
        String path = "/v1/occupations/-1";
        mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("occupationId: value must be positive")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testCreateOccupation() throws Exception {
        // Given
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        OccupationToCreateDto occupationToCreateDto = new OccupationToCreateDto(1L, "Occupation test name");
        Group groupNotPersisted = new Group(1L, null);
        Occupation occupationNotPersisted = new Occupation(null, groupNotPersisted, "Occupation test name");
        GroupDto groupDto = new GroupDto(1L, "Group test name");
        OccupationDto occupationDto = new OccupationDto(1L, groupDto, "Occupation test name");

        when(occupationMapper.mapToOccupation(occupationToCreateDto)).thenReturn(occupationNotPersisted);
        when(db.saveOccupation(occupationNotPersisted)).thenReturn(occupation);
        when(occupationMapper.mapToOccupationDto(occupation)).thenReturn(occupationDto);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(occupationToCreateDto);

        // When & Then
        mockMvc.perform(post("/v1/occupations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.group.id", is(1)))
                .andExpect(jsonPath("$.group.name", is("Group test name")))
                .andExpect(jsonPath("$.name", is("Occupation test name")));
    }

    @Test
    public void testCreateOccupationWhenNoSuchGroup() throws Exception {
        // Given
        OccupationToCreateDto occupationToCreateDto = new OccupationToCreateDto(1L, "Occupation test name");
        Group groupNotPersisted = new Group(1L, null);
        Occupation occupationNotPersisted = new Occupation(null, groupNotPersisted, "Occupation test name");

        when(occupationMapper.mapToOccupation(occupationToCreateDto)).thenReturn(occupationNotPersisted);
        when(db.saveOccupation(occupationNotPersisted)).thenThrow(new NoSuchObjectException(Group.class, 1L));

        Gson gson = new Gson();
        String jsonContent = gson.toJson(occupationToCreateDto);

        // When & Then
        String path = "/v1/occupations";
        mockMvc.perform(post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Group object with id=1 does not exist")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testCreateOccupationWhenNullGroupAndName() throws Exception {
        // Given
        OccupationToCreateDto occupationToCreateDto = new OccupationToCreateDto(null, null);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(occupationToCreateDto);

        // When & Then
        String path = "/v1/occupations";
        mockMvc.perform(post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Not valid fields")))
                .andExpect(jsonPath("$.details", containsInAnyOrder("groupId: must not be null",
                                                                    "name: must not be null")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testCreateOccupationWhenTooShortNameAndNonpositiveGroupId() throws Exception {
        // Given
        OccupationToCreateDto occupationToCreateDto = new OccupationToCreateDto(-1L, "A");

        Gson gson = new Gson();
        String jsonContent = gson.toJson(occupationToCreateDto);

        // When & Then
        String path = "/v1/occupations";
        mockMvc.perform(post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Not valid fields")))
                .andExpect(jsonPath("$.details", containsInAnyOrder("groupId: must be greater than 0",
                                                                    "name: size must be between 2 and 255")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testCreateOccupationWhenDataIntegrityViolation() throws Exception {
        // Given
        OccupationToCreateDto occupationToCreateDto = new OccupationToCreateDto(1L, "Occupation test name");
        Group groupNotPersisted = new Group(1L, null);
        Occupation occupationNotPersisted = new Occupation(null, groupNotPersisted, "Occupation test name");

        when(occupationMapper.mapToOccupation(occupationToCreateDto)).thenReturn(occupationNotPersisted);
        String causeMessage = "Cause test message";
        when(db.saveOccupation(occupationNotPersisted))
                .thenThrow(new DataIntegrityViolationException("Test message", new RuntimeException(causeMessage)));

        Gson gson = new Gson();
        String jsonContent = gson.toJson(occupationToCreateDto);

        // When & Then
        String path = "/v1/occupations";
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
    public void testUpdateOccupation() throws Exception {
        // Given
        Group group = new Group(1L, "Group test name");
        Occupation occupation = new Occupation(1L, group, "Occupation test name");
        OccupationToUpdateDto occupationToUpdateDto = new OccupationToUpdateDto(1L, 1L, "Occupation test name");
        Group groupNotPersisted = new Group(1L, null);
        Occupation occupationNotPersisted = new Occupation(1L, groupNotPersisted, "Occupation test name");
        GroupDto groupDto = new GroupDto(1L, "Group test name");
        OccupationDto occupationDto = new OccupationDto(1L, groupDto, "Occupation test name");

        when(occupationMapper.mapToOccupation(occupationToUpdateDto)).thenReturn(occupationNotPersisted);
        when(db.updateOccupation(occupationNotPersisted)).thenReturn(occupation);
        when(occupationMapper.mapToOccupationDto(occupation)).thenReturn(occupationDto);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(occupationToUpdateDto);

        // When & Then
        mockMvc.perform(put("/v1/occupations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.group.id", is(1)))
                .andExpect(jsonPath("$.group.name", is("Group test name")))
                .andExpect(jsonPath("$.name", is("Occupation test name")));
    }

    @Test
    public void testUpdateOccupationWhenNoSuchOccupation() throws Exception {
        // Given
        OccupationToUpdateDto occupationToUpdateDto = new OccupationToUpdateDto(1L, 1L, "Occupation test name");
        Group groupNotPersisted = new Group(1L, null);
        Occupation occupationNotPersisted = new Occupation(1L, groupNotPersisted, "Occupation test name");

        when(occupationMapper.mapToOccupation(occupationToUpdateDto)).thenReturn(occupationNotPersisted);
        when(db.updateOccupation(occupationNotPersisted)).thenThrow(new NoSuchObjectException(Occupation.class, 1L));

        Gson gson = new Gson();
        String jsonContent = gson.toJson(occupationToUpdateDto);

        // When & Then
        String path = "/v1/occupations";
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
    public void testUpdateOccupationWhenNoSuchGroup() throws Exception {
        // Given
        OccupationToUpdateDto occupationToUpdateDto = new OccupationToUpdateDto(1L, 1L, "Occupation test name");
        Group groupNotPersisted = new Group(1L, null);
        Occupation occupationNotPersisted = new Occupation(1L, groupNotPersisted, "Occupation test name");

        when(occupationMapper.mapToOccupation(occupationToUpdateDto)).thenReturn(occupationNotPersisted);
        when(db.updateOccupation(occupationNotPersisted)).thenThrow(new NoSuchObjectException(Group.class, 1L));

        Gson gson = new Gson();
        String jsonContent = gson.toJson(occupationToUpdateDto);

        // When & Then
        String path = "/v1/occupations";
        mockMvc.perform(put(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Group object with id=1 does not exist")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testUpdateOccupationWhenNullIdAndNullName() throws Exception {
        // Given
        OccupationToUpdateDto occupationToUpdateDto = new OccupationToUpdateDto(null, null, null);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(occupationToUpdateDto);

        // When & Then
        String path = "/v1/occupations";
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
                                                                    "groupId: must not be null",
                                                                    "name: must not be null")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testUpdateOccupationWhenNonpositiveIdTooShortNameAndNonpositiveGroupId() throws Exception {
        // Given
        OccupationToUpdateDto occupationToUpdateDto = new OccupationToUpdateDto(-1L, -1L, "A");

        Gson gson = new Gson();
        String jsonContent = gson.toJson(occupationToUpdateDto);

        // When & Then
        String path = "/v1/occupations";
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
                                                                    "groupId: must be greater than 0",
                                                                    "name: size must be between 2 and 255")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testUpdateOccupationWhenDataIntegrityViolation() throws Exception {
        // Given
        OccupationToUpdateDto occupationToUpdateDto = new OccupationToUpdateDto(1L, 1L, "Occupation test name");
        Group groupNotPersisted = new Group(1L, null);
        Occupation occupationNotPersisted = new Occupation(1L, groupNotPersisted, "Occupation test name");

        when(occupationMapper.mapToOccupation(occupationToUpdateDto)).thenReturn(occupationNotPersisted);
        String causeMessage = "Cause test message";
        when(db.updateOccupation(occupationNotPersisted))
                .thenThrow(new DataIntegrityViolationException("Test message", new RuntimeException(causeMessage)));

        Gson gson = new Gson();
        String jsonContent = gson.toJson(occupationToUpdateDto);

        // When & Then
        String path = "/v1/occupations";
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
    public void testDeleteOccupation() throws Exception {
        // Given
        PositiveLongInQueryDto idDto = new PositiveLongInQueryDto(Long.MAX_VALUE);
        when(queryVariablesMapper.mapToLong(idDto)).thenReturn(Long.MAX_VALUE);

        // When & Then
        mockMvc.perform(delete("/v1/occupations/" + Long.MAX_VALUE).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(db, times(1)).deleteOccupation(Long.MAX_VALUE);
    }

    @Test
    public void testDeleteOccupationWhenNoSuchOccupation() throws Exception {
        // Given
        PositiveLongInQueryDto idDto = new PositiveLongInQueryDto(Long.MAX_VALUE);

        when(queryVariablesMapper.mapToLong(idDto)).thenReturn(Long.MAX_VALUE);
        doThrow(new NoSuchObjectException(Occupation.class, Long.MAX_VALUE)).when(db).deleteOccupation(Long.MAX_VALUE);

        // When & Then
        String path = "/v1/occupations/" + Long.MAX_VALUE;
        mockMvc.perform(delete(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Occupation object with id=" + Long.MAX_VALUE + " does not exist")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testDeleteOccupationWhenDataIntegrityViolation() throws Exception {
        // Given
        PositiveLongInQueryDto idDto = new PositiveLongInQueryDto(Long.MAX_VALUE);

        when(queryVariablesMapper.mapToLong(idDto)).thenReturn(Long.MAX_VALUE);
        String causeMessage = "Cause test message";
        doThrow(new DataIntegrityViolationException("Test message", new RuntimeException(causeMessage)))
                .when(db).deleteOccupation(Long.MAX_VALUE);

        // When & Then
        String path = "/v1/occupations/" + Long.MAX_VALUE;
        mockMvc.perform(delete(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is(causeMessage)))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testDeleteOccupationWhenNonpositiveId() throws Exception {
        // Given & When & Then
        String path = "/v1/occupations/-1";
        mockMvc.perform(delete(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("occupationId: value must be positive")))
                .andExpect(jsonPath("$.path", is(path)));
    }
}