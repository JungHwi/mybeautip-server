package com.jocoos.mybeautip.global.advice;

import com.jocoos.mybeautip.global.exception.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Log4j2
@RestControllerAdvice
public class ControllerExceptionAdvice {

    private Environment environment;

    public ControllerExceptionAdvice(Environment environment) {
        this.environment = environment;
    }

    @ExceptionHandler(ConflictException.class)
    public final ResponseEntity<ErrorResponse> handleConflictException(ConflictException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(e.getMessage())
                .errorDescription(e.getDescription())
                .build();
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler({NotFoundException.class})
    public final ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(e.getMessage())
                .errorDescription(e.getDescription())
                .build();
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(e.getMessage())
                .errorDescription(e.getDescription())
                .build();
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(e.getMessage())
                .errorDescription(e.getDescription())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public final ResponseEntity<ErrorResponse> handleMemberNotFoundException(MemberNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(e.getMessage())
                .errorDescription(e.getDescription())
                .build();
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(e.getMessage())
                .errorDescription(request.getDescription(false))
                .build();
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(BAD_REQUEST.name().toLowerCase())
                .errorDescription(e.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }



    public ResponseEntity<Object> handleNumberFormatException(NumberFormatException e) {
        log.debug("handleNumberFormatException called");
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("bad_request")
                .errorDescription(e.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    private boolean isProduction() {
        if (environment == null) {
            return false;
        }
        return Arrays.asList(environment.getActiveProfiles()).contains("production");
    }
}
