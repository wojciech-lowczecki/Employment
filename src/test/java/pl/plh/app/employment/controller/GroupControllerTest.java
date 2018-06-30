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
import pl.plh.app.employment.mapper.GroupMapper;
import pl.plh.app.employment.mapper.QueryVariablesMapper;
import pl.plh.app.employment.service.GroupPersistService;
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
@WebMvcTest(GroupController.class)
public class GroupControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupPersistService db;

    @MockBean
    private GroupMapper groupMapper;

    @MockBean
    private QueryVariablesMapper queryVariablesMapper;

    @Test
    public void testGetGroups() throws Exception {
        // Given
        Group group = new Group(1L, "Group test name");
        List<Group> groupList = Arrays.asList(group);
        GroupDto groupDto = new GroupDto(1L, "Group test name");
        List<GroupDto> groupDtoList = Arrays.asList(groupDto);

        when(db.getAllGroups()).thenReturn(groupList);
        when(groupMapper.mapToGroupDtoList(groupList)).thenReturn(groupDtoList);

        // When & Then
        mockMvc.perform(get("/v1/groups").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Group test name")));
    }

    @Test
    public void testGetGroupsEmptyResult() throws Exception {
        // Given
        List<Group> groupList = new ArrayList<>();
        List<GroupDto> groupDtoList = new ArrayList<>();

        when(db.getAllGroups()).thenReturn(groupList);
        when(groupMapper.mapToGroupDtoList(groupList)).thenReturn(groupDtoList);

        // When & Then
        mockMvc.perform(get("/v1/groups").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetGroup() throws Exception {
        // Given
        Group group = new Group(1L, "Group test name");
        GroupDto groupDto = new GroupDto(1L, "Group test name");
        PositiveLongInQueryDto groupIdDto = new PositiveLongInQueryDto(1L);

        when(queryVariablesMapper.mapToLong(groupIdDto)).thenReturn(1L);
        when(db.getGroup(1L)).thenReturn(group);
        when(groupMapper.mapToGroupDto(group)).thenReturn(groupDto);

        // When & Then
        mockMvc.perform(get("/v1/groups/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Group test name")));
    }

    @Test
    public void testGetGroupWhenNonpositiveId() throws Exception {
        // Given & When & Then
        String path = "/v1/groups/-1";
        mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("groupId: value must be positive")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testGetGroupWhenNoSuchGroup() throws Exception {
        // Given
        PositiveLongInQueryDto groupIdDto = new PositiveLongInQueryDto(1L);

        when(queryVariablesMapper.mapToLong(groupIdDto)).thenReturn(1L);
        when(db.getGroup(1L)).thenThrow(new NoSuchObjectException(Group.class, 1L));

        // When & Then
        String path = "/v1/groups/1";
        mockMvc.perform(get(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Group object with id=1 does not exist")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testCreateGroup() throws Exception {
        // Given
        Group group = new Group(1L, "Group test name");
        GroupToCreateDto groupToCreateDto =  new GroupToCreateDto("Group test name");
        Group groupNotPersisted = new Group(null, "Group test name");
        GroupDto groupDto = new GroupDto(1L, "Group test name");

        when(groupMapper.mapToGroup(groupToCreateDto)).thenReturn(groupNotPersisted);
        when(db.saveGroup(groupNotPersisted)).thenReturn(group);
        when(groupMapper.mapToGroupDto(group)).thenReturn(groupDto);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(groupToCreateDto);

        // When & Then
        mockMvc.perform(post("/v1/groups")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Group test name")));
    }

    @Test
    public void testCreateGroupWhenNullName() throws Exception {
        // Given
        GroupToCreateDto groupToCreateDto =  new GroupToCreateDto(null);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(groupToCreateDto);

        // When & Then
        String path = "/v1/groups";
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
    public void testCreateGroupWhenDataIntegrityViolation() throws Exception {
        // Given
        Group group = new Group(1L, "Group test name");
        GroupToCreateDto groupToCreateDto =  new GroupToCreateDto("Group test name");
        Group groupNotPersisted = new Group(null, "Group test name");
        GroupDto groupDto = new GroupDto(1L, "Group test name");

        when(groupMapper.mapToGroup(groupToCreateDto)).thenReturn(groupNotPersisted);
        String causeMessage = "Cause test message";
        when(db.saveGroup(groupNotPersisted))
                .thenThrow(new DataIntegrityViolationException("Test message", new RuntimeException(causeMessage)));

        Gson gson = new Gson();
        String jsonContent = gson.toJson(groupToCreateDto);

        // When & Then
        String path = "/v1/groups";
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
    public void testCreateGroupWhenTooShortName() throws Exception {
        // Given
        GroupToCreateDto groupToCreateDto =  new GroupToCreateDto("A");

        Gson gson = new Gson();
        String jsonContent = gson.toJson(groupToCreateDto);

        // When & Then
        String path = "/v1/groups";
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
    public void testUpdateGroup() throws Exception {
        // Given
        Group group = new Group(1L, "Group test name");
        GroupDto groupDto = new GroupDto(1L, "Group test name");

        when(groupMapper.mapToGroup(groupDto)).thenReturn(group);
        when(db.updateGroup(group)).thenReturn(group);
        when(groupMapper.mapToGroupDto(group)).thenReturn(groupDto);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(groupDto);

        // When & Then
        mockMvc.perform(put("/v1/groups")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Group test name")));
    }

    @Test
    public void testUpdateGroupWhenTooShortNameAndNonpositiveId() throws Exception {
        // Given
        GroupDto groupDto =  new GroupDto(0L, "A");

        Gson gson = new Gson();
        String jsonContent = gson.toJson(groupDto);

        // When & Then
        String path = "/v1/groups";
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
    public void testUpdateGroupWhenNullNameAndNullId() throws Exception {
        // Given
        GroupDto groupDto =  new GroupDto(null, null);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(groupDto);

        // When & Then
        String path = "/v1/groups";
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
    public void testUpdateGroupWhenNoSuchGroup() throws Exception {
        // Given
        Group group = new Group(1L, "Group test name");
        GroupDto groupDto = new GroupDto(1L, "Group test name");

        when(groupMapper.mapToGroup(groupDto)).thenReturn(group);
        when(db.updateGroup(group)).thenThrow(new NoSuchObjectException(Group.class, 1L));

        Gson gson = new Gson();
        String jsonContent = gson.toJson(groupDto);

        // When & Then
        String path = "/v1/groups";
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
    public void testUpdateGroupWhenDataIntegrityViolation() throws Exception {
        // Given
        Group group = new Group(1L, "Group test name");
        GroupDto groupDto =  new GroupDto(1L, "Group test name");

        when(groupMapper.mapToGroup(groupDto)).thenReturn(group);
        String causeMessage = "Cause test message";
        when(db.updateGroup(group))
                .thenThrow(new DataIntegrityViolationException("Test message", new RuntimeException(causeMessage)));

        Gson gson = new Gson();
        String jsonContent = gson.toJson(groupDto);

        // When & Then
        String path = "/v1/groups";
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
    public void testDeleteGroup() throws Exception {
        // Given
        PositiveLongInQueryDto idDto = new PositiveLongInQueryDto(Long.MAX_VALUE);
        when(queryVariablesMapper.mapToLong(idDto)).thenReturn(Long.MAX_VALUE);

        // When & Then
        mockMvc.perform(delete("/v1/groups/" + Long.MAX_VALUE).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(db, times(1)).deleteGroup(Long.MAX_VALUE);
    }

    @Test
    public void testDeleteGroupWhenDataIntegrityViolation() throws Exception {
        // Given
        PositiveLongInQueryDto idDto = new PositiveLongInQueryDto(Long.MAX_VALUE);
        when(queryVariablesMapper.mapToLong(idDto)).thenReturn(Long.MAX_VALUE);
        String causeMessage = "Cause test message";
        doThrow(new DataIntegrityViolationException("Test message", new RuntimeException(causeMessage)))
                .when(db).deleteGroup(Long.MAX_VALUE);

        // When & Then
        String path = "/v1/groups/" + Long.MAX_VALUE;
        mockMvc.perform(delete(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is(causeMessage)))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testDeleteGroupWhenNoSuchGroup() throws Exception {
        // Given
        PositiveLongInQueryDto idDto = new PositiveLongInQueryDto(Long.MAX_VALUE);
        when(queryVariablesMapper.mapToLong(idDto)).thenReturn(Long.MAX_VALUE);
        doThrow(new NoSuchObjectException(Occupation.class, Long.MAX_VALUE)).when(db).deleteGroup(Long.MAX_VALUE);

        // When & Then
        String path = "/v1/groups/" + Long.MAX_VALUE;
        mockMvc.perform(delete(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Occupation object with id=" + Long.MAX_VALUE + " does not exist")))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void testDeleteGroupWhenNonpositiveId() throws Exception {
        // Given & When & Then
        String path = "/v1/groups/-1";
        mockMvc.perform(delete(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", not(empty())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("groupId: value must be positive")))
                .andExpect(jsonPath("$.path", is(path)));
    }
}