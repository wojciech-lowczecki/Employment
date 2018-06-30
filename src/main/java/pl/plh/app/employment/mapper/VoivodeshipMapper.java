package pl.plh.app.employment.mapper;

import org.springframework.stereotype.Component;
import pl.plh.app.employment.domain.Voivodeship;
import pl.plh.app.employment.domain.VoivodeshipDto;
import pl.plh.app.employment.domain.VoivodeshipToCreateDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class VoivodeshipMapper {
    public VoivodeshipDto mapToVoivodeshipDto(Voivodeship voivodeship) {
        return new VoivodeshipDto(voivodeship.getId(), voivodeship.getName());
    }

    public List<VoivodeshipDto> mapToVoivodeshipDtoList(List<Voivodeship> voivodeshipList) {
        return voivodeshipList.stream()
                .map(this::mapToVoivodeshipDto)
                .collect(toList());
    }

    public Voivodeship mapToVoivodeship(VoivodeshipDto voivodeshipDto) {
        return new Voivodeship(voivodeshipDto.getId(), voivodeshipDto.getName());
    }

    public Voivodeship mapToVoivodeship(VoivodeshipToCreateDto voivodeshipToCreateDto) {
        return new Voivodeship(null, voivodeshipToCreateDto.getName());
    }

    public Voivodeship mapToVoivodeship(Long id) {
        return new Voivodeship(id, null);
    }

}
