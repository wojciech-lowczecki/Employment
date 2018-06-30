package pl.plh.app.employment.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.plh.app.employment.domain.OccupationDto;
import pl.plh.app.employment.domain.OccupationToCreateDto;
import pl.plh.app.employment.domain.OccupationToUpdateDto;
import pl.plh.app.employment.domain.PositiveLongInQueryDto;
import pl.plh.app.employment.mapper.OccupationMapper;
import pl.plh.app.employment.mapper.QueryVariablesMapper;
import pl.plh.app.employment.service.OccupationPersistService;
import pl.plh.app.employment.swagger.ApiCUDResponses;
import pl.plh.app.employment.swagger.ApiReadResponses;

import javax.validation.Valid;
import java.util.List;

@Api(description = "CRUD operations", tags = {"Occupation"})
@RestController
@RequestMapping("/v1")
public class OccupationController {
    @Autowired
    private OccupationPersistService db;

    @Autowired
    private OccupationMapper occupationMapper;

    @Autowired
    private QueryVariablesMapper queryVariablesMapper;

    @ApiOperation(value = "Get all occupations", tags = {"Occupation"})
    @RequestMapping(method = RequestMethod.GET, value = "/occupations", produces = "application/json")
    public List<OccupationDto> getOccupations() {
        return occupationMapper.mapToOccupationDtoList(db.getAllOccupations());
    }

    @ApiOperation(value = "Get an occupation by identifier", tags = {"Occupation"})
    @ApiReadResponses
    @RequestMapping(method = RequestMethod.GET, value = "/occupations/{occupationId}", produces = "application/json")
    public OccupationDto getOccupation(@ApiParam(value = "Occupation identifier", required = true)
                                       @PathVariable PositiveLongInQueryDto occupationId) {
        return occupationMapper.mapToOccupationDto(db.getOccupation(queryVariablesMapper.mapToLong(occupationId)));
    }

    @ApiOperation(value = "Create a new occupation", tags = {"Occupation"})
    @ApiCUDResponses
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, value = "/occupations", consumes = "application/json",
                    produces = "application/json")
    public OccupationDto createOccupation(@ApiParam(name = "occupationToCreate", value = "Occupation to create",
                                                    required = true)
                                          @Valid @RequestBody OccupationToCreateDto occupationToCreateDto) {
        return occupationMapper.mapToOccupationDto(
                db.saveOccupation(occupationMapper.mapToOccupation(occupationToCreateDto)));
    }

    @ApiOperation(value = "Update an occupation", tags = {"Occupation"})
    @ApiCUDResponses
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, value = "/occupations", consumes = "application/json",
                    produces = "application/json")
    public OccupationDto updateOccupation(@ApiParam(name = "occupationToUpdate", value = "Occupation to update",
                                                    required = true)
                                          @Valid @RequestBody OccupationToUpdateDto occupationToUpdateDto) {
        return occupationMapper.mapToOccupationDto(
                db.updateOccupation(occupationMapper.mapToOccupation(occupationToUpdateDto)));
    }

    @ApiOperation(value = "Delete an occupation by identifier", tags = {"Occupation"})
    @ApiCUDResponses
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE, value = "/occupations/{occupationId}")
    public void deleteOccupation(@ApiParam(value = "Occupation identifier", required = true)
                                 @PathVariable PositiveLongInQueryDto occupationId) {
        db.deleteOccupation(queryVariablesMapper.mapToLong(occupationId));
    }
}