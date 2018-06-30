package pl.plh.app.employment.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.plh.app.employment.domain.PersonDto;
import pl.plh.app.employment.domain.PersonToCreateDto;
import pl.plh.app.employment.domain.PersonToUpdateDto;
import pl.plh.app.employment.domain.PositiveLongInQueryDto;
import pl.plh.app.employment.mapper.PersonMapper;
import pl.plh.app.employment.mapper.QueryVariablesMapper;
import pl.plh.app.employment.service.PersonPersistService;
import pl.plh.app.employment.swagger.ApiCUDResponses;
import pl.plh.app.employment.swagger.ApiReadResponses;

import javax.validation.Valid;
import java.util.List;

@Api(description = "CRUD operations", tags = {"Person"})
@RestController
@RequestMapping("/v1")
public class PersonController {
    @Autowired
    private PersonPersistService db;

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private QueryVariablesMapper queryVariablesMapper;

    @ApiOperation(value = "Get all persons", tags = {"Person"})
    @RequestMapping(method = RequestMethod.GET, value = "/persons", produces = "application/json")
    public List<PersonDto> getPersons() {
        return personMapper.mapToPersonDtoList(db.getAllPersons());
    }

    @ApiOperation(value = "Get a person by identifier", tags = {"Person"})
    @ApiReadResponses
    @RequestMapping(method = RequestMethod.GET, value = "/persons/{personId}", produces = "application/json")
    public PersonDto getPerson(@ApiParam(value = "Person identifier", required = true)
                               @PathVariable PositiveLongInQueryDto personId) {
        return personMapper.mapToPersonDto(db.getPerson(queryVariablesMapper.mapToLong(personId)));
    }

    @ApiOperation(value = "Create a new person", tags = {"Person"})
    @ApiCUDResponses
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, value = "/persons", consumes = "application/json",
                    produces = "application/json")
    public PersonDto createPerson(@ApiParam(name = "personToCreate", value = "Person to create", required = true)
                                  @Valid @RequestBody PersonToCreateDto personToCreateDto) {
        return personMapper.mapToPersonDto(db.savePerson(personMapper.mapToPerson(personToCreateDto)));
    }

    @ApiOperation(value = "Update a person", tags = {"Person"})
    @ApiCUDResponses
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, value = "/persons", consumes = "application/json",
                    produces = "application/json")
    public PersonDto updatePerson(@ApiParam(name = "personToUpdate", value = "Person to update", required = true)
                                  @Valid @RequestBody PersonToUpdateDto personToUpdateDto) {
        return personMapper.mapToPersonDto(db.updatePerson(personMapper.mapToPerson(personToUpdateDto)));
    }

    @ApiOperation(value = "Delete a person by identifier", tags = {"Person"})
    @ApiCUDResponses
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE, value = "/persons/{personId}")
    public void deletePerson(@ApiParam(value = "Person identifier", required = true)
                             @PathVariable PositiveLongInQueryDto personId) {
        db.deletePerson(queryVariablesMapper.mapToLong(personId));
    }
}