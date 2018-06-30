package pl.plh.app.employment.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel(value = "Voivodeship to create")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(doNotUseGetters = true)
public class VoivodeshipToCreateDto {
    @ApiModelProperty(value = "Voivodeship name", example = "Name", required = true, allowableValues = "range[2, 255]")
    @NotNull
    @Size(min = 2, max = 255)
    private String name;
}
