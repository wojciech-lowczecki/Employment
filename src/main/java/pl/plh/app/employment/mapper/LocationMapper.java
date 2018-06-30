package pl.plh.app.employment.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.plh.app.employment.domain.Location;
import pl.plh.app.employment.domain.LocationDto;
import pl.plh.app.employment.domain.LocationToCreateDto;
import pl.plh.app.employment.domain.LocationToUpdateDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class LocationMapper {
    @Autowired
    private VoivodeshipMapper voivodeshipMapper;

    LocationMapper(VoivodeshipMapper voivodeshipMapper) {
        this.voivodeshipMapper = voivodeshipMapper;
    }

    public LocationDto mapToLocationDto(Location location) {
        return new LocationDto(
                location.getId(),
                voivodeshipMapper.mapToVoivodeshipDto(location.getVoivodeship()),
                location.getName(),
                location.getPopulation()
        );
    }

    public List<LocationDto> mapToLocationDtoList(List<Location> locationList) {
        return locationList.stream()
                .map(this::mapToLocationDto)
                .collect(toList());
    }

    public Location mapToLocation(LocationDto locationDto) {
        return new Location(
                locationDto.getId(),
                voivodeshipMapper.mapToVoivodeship(locationDto.getVoivodeship()),
                locationDto.getName(),
                locationDto.getPopulation()
        );
    }

    public Location mapToLocation(LocationToUpdateDto locationToUpdateDto) {
        return new Location(
                locationToUpdateDto.getId(),
                voivodeshipMapper.mapToVoivodeship(locationToUpdateDto.getVoivodeshipId()),
                locationToUpdateDto.getName(),
                locationToUpdateDto.getPopulation()
        );
    }

    public Location mapToLocation(LocationToCreateDto locationToCreateDto) {
        return new Location(
                null,
                voivodeshipMapper.mapToVoivodeship(locationToCreateDto.getVoivodeshipId()),
                locationToCreateDto.getName(),
                locationToCreateDto.getPopulation()
        );
    }

    public Location mapToLocation(Long id) {
        return new Location(id, null, null, null);
    }
}
