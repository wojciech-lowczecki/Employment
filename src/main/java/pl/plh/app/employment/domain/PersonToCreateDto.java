package pl.plh.app.employment.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@ApiModel(value = "Person to create")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters = true)
public class PersonToCreateDto {
    @ApiModelProperty(value = "PESEL identifier", example = "85123112345", required = true,
                      allowableValues = "range[11, 11]")
    @NotNull
    @Size(min = 11, max = 11)
    private String pesel;

    @ApiModelProperty(value = "Date of birth", example = "2000-12-31", required = true)
    @NotNull
    @Past
    private LocalDate birthDate;

    @ApiModelProperty(value = "Gender", example = "m", required = true)
    @NotNull
    private GenderDto gender;

    @ApiModelProperty(value = "First name", example = "John", required = true, allowableValues = "range[2, 255]")
    @NotNull
    @Size(min = 2, max = 255)
    private String firstName;

    @ApiModelProperty(value = "Last name", example = "Smith", required = true, allowableValues = "range[2, 255]")
    @NotNull
    @Size(min = 2, max = 255)
    private String lastName;

    @ApiModelProperty(value = "Location identifier", example = "1", required = true,
                      allowableValues = "range[1, infinity]")
    @NotNull
    @Positive
    private Long locationId;

    @ApiModelProperty(value = "Occupation identifiers list (size: 0-unemployed, 3-maximum)", example = "[1,2,3]",
                      required = true)
    @NotNull
    @Size(max = 3)
    private List<@NotNull @Positive Long> occupationIds;
}
