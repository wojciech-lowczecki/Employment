package pl.plh.app.employment.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.plh.app.employment.domain.PositiveLongInQueryDto;
import pl.plh.app.employment.domain.VoivodeshipDto;
import pl.plh.app.employment.domain.VoivodeshipToCreateDto;
import pl.plh.app.employment.mapper.QueryVariablesMapper;
import pl.plh.app.employment.mapper.VoivodeshipMapper;
import pl.plh.app.employment.service.VoivodeshipPersistService;
import pl.plh.app.employment.swagger.ApiCUDResponses;
import pl.plh.app.employment.swagger.ApiReadResponses;

import javax.validation.Valid;
import java.util.List;

@Api(description = "CRUD operations", tags = {"Voivodeship"})
@RestController
@RequestMapping("/v1")
public class VoivodeshipController {
    @Autowired
    private VoivodeshipPersistService db;

    @Autowired
    private VoivodeshipMapper voivodeshipMapper;

    @Autowired
    private QueryVariablesMapper queryVariablesMapper;

    @ApiOperation(value = "Get all voivodeships")
    @RequestMapping(method = RequestMethod.GET, value = "/voivodeships", produces = "application/json")
    public List<VoivodeshipDto> getVoivodeships() {
        return voivodeshipMapper.mapToVoivodeshipDtoList(db.getAllVoivodeships());
    }

    @ApiOperation(value = "Get a voivodeship by identifier", tags = {"Voivodeship"})
    @ApiReadResponses
    @RequestMapping(method = RequestMethod.GET, value = "/voivodeships/{voivodeshipId}", produces = "application/json")
    public VoivodeshipDto getVoivodeship(@ApiParam(value = "Voivodeship identifier", required = true)
                                         @PathVariable PositiveLongInQueryDto voivodeshipId) {
        return voivodeshipMapper.mapToVoivodeshipDto(db.getVoivodeship(queryVariablesMapper.mapToLong(voivodeshipId)));
    }

    @ApiOperation(value = "Create a new voivodeship", tags = {"Voivodeship"})
    @ApiCUDResponses
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, value = "/voivodeships", consumes = "application/json",
                    produces = "application/json")
    public VoivodeshipDto createVoivodeship(@ApiParam(name = "voivodeshipToCreate", value = "Voivodeship to create",
                                                      required = true)
                                            @Valid @RequestBody VoivodeshipToCreateDto voivodeshipToCreateDto) {
        return voivodeshipMapper.mapToVoivodeshipDto(
                db.saveVoivodeship(voivodeshipMapper.mapToVoivodeship(voivodeshipToCreateDto)));
    }

    @ApiOperation(value = "Update a voivodeship", tags = {"Voivodeship"})
    @ApiCUDResponses
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, value = "/voivodeships", consumes = "application/json",
                    produces = "application/json")
    public VoivodeshipDto updateVoivodeship(@ApiParam(name = "voivodeship", value = "Voivodeship", required = true)
                                            @Valid @RequestBody VoivodeshipDto voivodeshipDto) {
        return voivodeshipMapper.mapToVoivodeshipDto(
                db.updateVoivodeship(voivodeshipMapper.mapToVoivodeship(voivodeshipDto)));
    }

    @ApiOperation(value = "Delete a voivodeship by identifier", tags = {"Voivodeship"})
    @ApiCUDResponses
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE, value = "/voivodeships/{voivodeshipId}")
    public void deleteVoivodeship(@ApiParam(value = "Voivodeship identifier", required = true)
                                  @PathVariable PositiveLongInQueryDto voivodeshipId) {
        db.deleteVoivodeship(queryVariablesMapper.mapToLong(voivodeshipId));
    }
}