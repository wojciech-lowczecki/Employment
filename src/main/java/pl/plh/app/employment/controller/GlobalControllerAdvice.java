package pl.plh.app.employment.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import pl.plh.app.employment.domain.PositiveLongInQueryDto;
import pl.plh.app.employment.service.NoSuchObjectException;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalControllerAdvice extends DefaultHandlerExceptionResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    @ExceptionHandler(NoSuchObjectException.class)
    @ResponseBody
    public ResponseEntity<ErrorDataDto> handleNoSuchObject(NoSuchObjectException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND; // 404 - Not Found
        ErrorDataDto errorDataDto = new ErrorDataDto(status, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(errorDataDto, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ErrorDataDto> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                     HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400 - Bad Request
        String message = "Not valid fields";
        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + ": " + error.getDefaultMessage());
        }
        ErrorDataDto errorDataDto = new ErrorDataDto(status, message, details, request.getRequestURI());
        return new ResponseEntity<>(errorDataDto, status);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    public ResponseEntity<ErrorDataDto> handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                                                     HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT; // 409 - Conflict
        ErrorDataDto errorDataDto = new ErrorDataDto(status, ex.getMostSpecificCause().getMessage(),
                                                     request.getRequestURI());
        return new ResponseEntity<>(errorDataDto, status);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResponseEntity<ErrorDataDto> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                         HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400 - Bad Request
        String message = ex.getName() + ": " + ex.getMostSpecificCause().getMessage();
        ErrorDataDto errorDataDto = new ErrorDataDto(status, message, request.getRequestURI());
        return new ResponseEntity<>(errorDataDto, status);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(PositiveLongInQueryDto.class, new PropertyEditorSupport() {
            @Override
            public String getAsText() {
                return this.getValue().toString();
            }

            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(new PositiveLongInQueryDto(text));
            }
        });
    }
}