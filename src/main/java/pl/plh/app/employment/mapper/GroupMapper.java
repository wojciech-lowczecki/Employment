package pl.plh.app.employment.mapper;

import org.springframework.stereotype.Component;
import pl.plh.app.employment.domain.Group;
import pl.plh.app.employment.domain.GroupDto;
import pl.plh.app.employment.domain.GroupToCreateDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class GroupMapper {
    public GroupDto mapToGroupDto(Group group) {
        return new GroupDto(group.getId(), group.getName());
    }

    public List<GroupDto> mapToGroupDtoList(List<Group> groupList) {
        return groupList.stream()
                .map(this::mapToGroupDto)
                .collect(toList());
    }

    public Group mapToGroup(GroupDto groupDto) {
        return new Group(groupDto.getId(), groupDto.getName());
    }

    public Group mapToGroup(GroupToCreateDto groupToCreateDto) {
        return new Group(null, groupToCreateDto.getName());
    }

    public Group mapToGroup(Long id) {
        return new Group(id, null);
    }
}
