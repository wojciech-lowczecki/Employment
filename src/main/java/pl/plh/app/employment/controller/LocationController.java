package pl.plh.app.employment.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.plh.app.employment.domain.LocationDto;
import pl.plh.app.employment.domain.LocationToCreateDto;
import pl.plh.app.employment.domain.LocationToUpdateDto;
import pl.plh.app.employment.domain.PositiveLongInQueryDto;
import pl.plh.app.employment.mapper.LocationMapper;
import pl.plh.app.employment.mapper.QueryVariablesMapper;
import pl.plh.app.employment.service.LocationPersistService;
import pl.plh.app.employment.swagger.ApiCUDResponses;
import pl.plh.app.employment.swagger.ApiReadResponses;

import javax.validation.Valid;
import java.util.List;

@Api(description = "CRUD operations", tags = {"Location"})
@RestController
@RequestMapping("/v1")
public class LocationController {
    @Autowired
    private LocationPersistService db;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private QueryVariablesMapper queryVariablesMapper;

    @ApiOperation(value = "Get all locations", tags = {"Location"})
    @RequestMapping(method = RequestMethod.GET, value = "/locations", produces = "application/json")
    public List<LocationDto> getLocations() {
        return locationMapper.mapToLocationDtoList(db.getAllLocations());
    }

    @ApiOperation(value = "Get a location by identifier", tags = {"Location"})
    @ApiReadResponses
    @RequestMapping(method = RequestMethod.GET, value = "/locations/{locationId}", produces = "application/json")
    public LocationDto getLocation(@ApiParam(value = "Location identifier", required = true)
                                   @PathVariable PositiveLongInQueryDto locationId) {
        return locationMapper.mapToLocationDto(db.getLocation(queryVariablesMapper.mapToLong(locationId)));
    }

    @ApiOperation(value = "Create a new location", tags = {"Location"})
    @ApiCUDResponses
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, value = "/locations", consumes = "application/json",
                    produces = "application/json")
    public LocationDto createLocation(@ApiParam(name = "locationToCreate", value = "Location to create", required = true)
                                      @Valid @RequestBody LocationToCreateDto locationToCreateDto) {
        return locationMapper.mapToLocationDto(db.saveLocation(locationMapper.mapToLocation(locationToCreateDto)));
    }

    @ApiOperation(value = "Update a location", tags = {"Location"})
    @ApiCUDResponses
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, value = "/locations", consumes = "application/json",
                    produces = "application/json")
    public LocationDto updateLocation(@ApiParam(name = "locationToUpdate", value = "Location to update", required = true)
                                      @Valid @RequestBody LocationToUpdateDto locationToUpdateDto) {
        return locationMapper.mapToLocationDto(db.updateLocation(locationMapper.mapToLocation(locationToUpdateDto)));
    }

    @ApiOperation(value = "Delete a location by identifier", tags = {"Location"})
    @ApiCUDResponses
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE, value = "/locations/{locationId}")
    public void deleteLocation(@ApiParam(value = "Location identifier", required = true)
                               @PathVariable PositiveLongInQueryDto locationId) {
        db.deleteLocation(queryVariablesMapper.mapToLong(locationId));
    }
}