package pl.plh.app.employment.mapper;

import org.junit.Test;
import pl.plh.app.employment.domain.PositiveLongInQueryDto;

import static org.junit.Assert.assertEquals;

public class QueryVariablesMapperTest {
    @Test
    public void testMapToLong() {
        // Given
        QueryVariablesMapper queryVariablesMapper = new QueryVariablesMapper();
        PositiveLongInQueryDto identifierInQueryDto = new PositiveLongInQueryDto(1234L);
        Long identifier = new Long(1234);

        // When & Then
        assertEquals(identifier, queryVariablesMapper.mapToLong(identifierInQueryDto));
    }
}