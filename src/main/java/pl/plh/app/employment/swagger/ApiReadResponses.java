package pl.plh.app.employment.swagger;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import pl.plh.app.employment.controller.ErrorDataDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Default responses for read requests
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({@ApiResponse(code = 400, message = "Bad Request", response = ErrorDataDto.class),
               @ApiResponse(code = 404, message = "Not Found", response = ErrorDataDto.class)})
public @interface ApiReadResponses {
    Class<?>[] exclude() default {};
}
