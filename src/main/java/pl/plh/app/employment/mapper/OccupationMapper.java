package pl.plh.app.employment.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.plh.app.employment.domain.Occupation;
import pl.plh.app.employment.domain.OccupationDto;
import pl.plh.app.employment.domain.OccupationToCreateDto;
import pl.plh.app.employment.domain.OccupationToUpdateDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class OccupationMapper {
    @Autowired
    private GroupMapper groupMapper;

    OccupationMapper(GroupMapper groupMapper) {
        this.groupMapper = groupMapper;
    }

    public OccupationDto mapToOccupationDto(Occupation occupation) {
        return new OccupationDto(
                occupation.getId(),
                groupMapper.mapToGroupDto(occupation.getGroup()),
                occupation.getName()
        );
    }

    public List<OccupationDto> mapToOccupationDtoList(List<Occupation> occupationList) {
        return occupationList.stream()
                .map(this::mapToOccupationDto)
                .collect(toList());
    }

    public Occupation mapToOccupation(OccupationDto occupationDto) {
        return new Occupation(
                occupationDto.getId(),
                groupMapper.mapToGroup(occupationDto.getGroup()),
                occupationDto.getName()
        );
    }

    public List<Occupation> mapToOccupationList(List<OccupationDto> occupationDtoList) {
        return occupationDtoList.stream()
                .map(this::mapToOccupation)
                .collect(toList());
    }

    public Occupation mapToOccupation(OccupationToUpdateDto occupationToUpdateDto) {
        return new Occupation(
                occupationToUpdateDto.getId(),
                groupMapper.mapToGroup(occupationToUpdateDto.getGroupId()),
                occupationToUpdateDto.getName()
        );
    }

    public Occupation mapToOccupation(OccupationToCreateDto occupationToCreateDto) {
        return new Occupation(
                null,
                groupMapper.mapToGroup(occupationToCreateDto.getGroupId()),
                occupationToCreateDto.getName()
        );
    }

    public Occupation mapToOccupation(Long id) {
        return new Occupation(id, null, null);
    }

    public List<Occupation> mapToOccupationListFromLongList(List<Long> idsList) {
        return idsList.stream()
                .map(this::mapToOccupation)
                .collect(toList());
    }
}
