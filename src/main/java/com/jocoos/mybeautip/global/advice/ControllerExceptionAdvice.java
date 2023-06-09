package com.jocoos.mybeautip.global.advice;

import com.jocoos.mybeautip.client.flipfloplite.exception.FFLException;
import com.jocoos.mybeautip.global.exception.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Log4j2
@RestControllerAdvice
public class ControllerExceptionAdvice {

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
                .contents(e.getContents())
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .toList();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(BAD_REQUEST.name().toLowerCase())
                .errorDescription(message.toString())
                .build();

        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public final ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(e.getErrorCode().name().toLowerCase())
                .errorDescription(e.getMessage())
                .contents(e.getContents())
                .build();
        return new ResponseEntity<>(errorResponse, e.getHttpStatus());
    }

    @ExceptionHandler(InternalServerException.class)
    public final ResponseEntity<ErrorResponse> handleFileIOException(InternalServerException e) {
        log.debug("Internal Server Exception Thrown ", e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(e.getErrorCode().name().toLowerCase())
                .errorDescription(e.getMessage())
                .build();
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(FFLException.class)
    public final ResponseEntity<ErrorResponse> handleFFLException(FFLException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(e.getErrorCode().name().toLowerCase())
                .errorDescription(e.getErrorMessage())
                .build();

        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }
}
