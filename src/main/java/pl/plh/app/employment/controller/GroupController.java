package pl.plh.app.employment.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.plh.app.employment.domain.GroupDto;
import pl.plh.app.employment.domain.GroupToCreateDto;
import pl.plh.app.employment.domain.PositiveLongInQueryDto;
import pl.plh.app.employment.mapper.GroupMapper;
import pl.plh.app.employment.mapper.QueryVariablesMapper;
import pl.plh.app.employment.service.GroupPersistService;
import pl.plh.app.employment.swagger.ApiCUDResponses;
import pl.plh.app.employment.swagger.ApiReadResponses;

import javax.validation.Valid;
import java.util.List;

@Api(description = "CRUD operations", tags = {"Group"})
@RestController
@RequestMapping("/v1")
public class GroupController {
    @Autowired
    private GroupPersistService db;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private QueryVariablesMapper queryVariablesMapper;

    @ApiOperation(position = 0, value = "Get all groups", tags = {"Group"})
    @RequestMapping(method = RequestMethod.GET, value = "/groups", produces = "application/json")
    public List<GroupDto> getGroups() {
        return groupMapper.mapToGroupDtoList(db.getAllGroups());
    }

    @ApiOperation(value = "Get a group by identifier", tags = {"Group"})
    @ApiReadResponses
    @RequestMapping(method = RequestMethod.GET, value = "/groups/{groupId}", produces = "application/json")
    public GroupDto getGroup(@ApiParam(value = "Group identifier", required = true)
                             @PathVariable PositiveLongInQueryDto groupId) {
        return groupMapper.mapToGroupDto(db.getGroup(queryVariablesMapper.mapToLong(groupId)));
    }

    @ApiOperation(value = "Create a new group", tags = {"Group"})
    @ApiCUDResponses
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, value = "/groups", consumes = "application/json",
                    produces = "application/json")
    public GroupDto createGroup(@ApiParam(name = "groupToCreate", value = "Group to create", required = true)
                                @Valid @RequestBody GroupToCreateDto groupToCreateDto) {
        return groupMapper.mapToGroupDto(db.saveGroup(groupMapper.mapToGroup(groupToCreateDto)));
    }

    @ApiOperation(value = "Update a group", tags = {"Group"})
    @ApiCUDResponses
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, value = "/groups", consumes = "application/json",
            produces = "application/json")
    public GroupDto updateGroup(@ApiParam(name = "group", value = "Group", required = true)
                                @Valid @RequestBody GroupDto groupDto) {
        return groupMapper.mapToGroupDto(db.updateGroup(groupMapper.mapToGroup(groupDto)));
    }

    @ApiOperation(value = "Delete a group by identifier", tags = {"Group"})
    @ApiCUDResponses
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE, value = "/groups/{groupId}")
    public void deleteGroup(@ApiParam(value = "Group identifier", required = true)
                            @PathVariable PositiveLongInQueryDto groupId) {
        db.deleteGroup(queryVariablesMapper.mapToLong(groupId));
    }
}