package pl.plh.app.employment.mapper;

import org.junit.Before;
import org.junit.Test;
import pl.plh.app.employment.domain.Group;
import pl.plh.app.employment.domain.GroupDto;
import pl.plh.app.employment.domain.GroupToCreateDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GroupMapperTest {
    private GroupMapper groupMapper;

    @Before
    public void initGroupMapper() {
        groupMapper = new GroupMapper();
    }

    @Test
    public void testMapToGroupDto() {
        // Given
        Group group = new Group(1L, "Group test name");
        GroupDto groupDto = new GroupDto(1L, "Group test name");

        // When @ Then
        assertEquals(groupDto, groupMapper.mapToGroupDto(group));
    }

    @Test
    public void testMapToGroupDtoList() {
        // Given
        Group group = new Group(1L, "Group test name");
        List<Group> groupList = Arrays.asList(group);
        GroupDto groupDto = new GroupDto(1L, "Group test name");
        List<GroupDto> groupDtoList = Arrays.asList(groupDto);

        // When @ Then
        assertEquals(groupDtoList, groupMapper.mapToGroupDtoList(groupList));
    }

    @Test
    public void testMapToGroupDtoListWhenEmpty() {
        // Given
        List<Group> groupList = new ArrayList<>();
        List<GroupDto> groupDtoList = new ArrayList<>();

        // When @ Then
        assertEquals(groupDtoList, groupMapper.mapToGroupDtoList(groupList));
    }

    @Test
    public void testMapToGroupFromGroupDto() {
        // Given
        Group group = new Group(1L, "Group test name");
        GroupDto groupDto = new GroupDto(1L, "Group test name");

        // When @ Then
        assertEquals(group, groupMapper.mapToGroup(groupDto));
    }

    @Test
    public void testMapToGroupFromGroupToCreateDto() {
        // Given
        Group group = new Group(null, "Group test name");
        GroupToCreateDto groupToCreateDto = new GroupToCreateDto("Group test name");

        // When @ Then
        assertEquals(group, groupMapper.mapToGroup(groupToCreateDto));
    }

    @Test
    public void testMapToGroupFromLong() {
        // Given
        Group group = new Group(1L, null);

        // When @ Then
        assertEquals(group, groupMapper.mapToGroup(1L));
    }
}