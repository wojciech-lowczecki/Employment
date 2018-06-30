package pl.plh.app.employment.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import pl.plh.app.employment.swagger.ApiErrorResponses;

import javax.servlet.http.HttpServletResponse;

@Api(description = "Default error page", tags = {"Restful Error"})
@RestController
public class RestfulErrorController implements ErrorController {
    private static final String PATH = "/error";

    @Autowired
    private ErrorAttributes errorAttributes;

    @ApiOperation(value = "Get error data", tags = {"Restful Error"})
    @ApiErrorResponses
    @RequestMapping(value = PATH, produces = "application/json")
    ResponseEntity<ErrorDataDto> error(WebRequest webRequest, HttpServletResponse response) {
        int status = response.getStatus();
        return ResponseEntity.status(status).body(
                new ErrorDataDto(status, errorAttributes.getErrorAttributes(webRequest, false))
        );
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
