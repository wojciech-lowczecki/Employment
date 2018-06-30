package pl.plh.app.employment.mapper;

import org.springframework.stereotype.Component;
import pl.plh.app.employment.domain.PositiveLongInQueryDto;

@Component
public class QueryVariablesMapper {
    public Long mapToLong(PositiveLongInQueryDto positiveLongInQueryDto) {
        return positiveLongInQueryDto.getValue();
    }
}
