package pl.plh.app.employment.swagger;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import pl.plh.app.employment.controller.ErrorDataDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Default responses for create, update and delete requests
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({@ApiResponse(code = 400, message = "Bad Request", response = ErrorDataDto.class),
               @ApiResponse(code = 404, message = "Not Found", response = ErrorDataDto.class),
               @ApiResponse(code = 409, message = "Conflict", response = ErrorDataDto.class)})
public @interface ApiCUDResponses {
    Class<?>[] exclude() default {};
}
