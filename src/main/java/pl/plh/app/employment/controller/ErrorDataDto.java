package pl.plh.app.employment.controller;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ApiModel(value = "Error data")
@Getter
public class ErrorDataDto {
    @ApiModelProperty(position = 0, value = "Error time")
    private Date timestamp;

    @ApiModelProperty(position = 1, value = "HTTP/1.1 status code", example = "999")
    private int status;

    @ApiModelProperty(position = 2, value = "HTTP/1.1 status name", example = "An error")
    private String error;

    @ApiModelProperty(position = 3, value = "Custom message", example = "Message")
    private String message;

    @ApiModelProperty(position = 4, allowEmptyValue = true, value = "Complementary messages",
                      example = "[\"Message 1\", \"Message 2\"]")
    private List<String> details;

    @ApiModelProperty(position = 5, value = "Request path", example = "/path")
    private String path;

    public ErrorDataDto(Date timestamp, int status, String error, String message, List<String> details, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
        this.path = path;
    }

    public ErrorDataDto(HttpStatus httpStatus, String message, List<String> details, String path) {
        this(new Date(), httpStatus.value(), httpStatus.getReasonPhrase(), message, details, path);
    }

    public ErrorDataDto(HttpStatus httpStatus, String message, String path) {
        this(httpStatus, message, Collections.emptyList(), path);
    }

    public ErrorDataDto(int status, Map<String, Object> errorAttributes) {
        this((Date) errorAttributes.get("timestamp"), status, (String) errorAttributes.get("error"),
             (String) errorAttributes.get("message"), Collections.emptyList(), (String) errorAttributes.get("path"));
    }
}
