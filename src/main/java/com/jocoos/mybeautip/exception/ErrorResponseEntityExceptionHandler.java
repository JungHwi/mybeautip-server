package com.jocoos.mybeautip.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
@RestController
public class ErrorResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ConflictException.class)
  public final ResponseEntity<ErrorResponse> handleConflictException(ConflictException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getDescription());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({NotFoundException.class})
  public final ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getDescription());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(BadRequestException.class)
  public final ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getDescription());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MemberNotFoundException.class)
  public final ResponseEntity<ErrorResponse> handleMemberNotFoundException(MemberNotFoundException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getDescription());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public final ResponseEntity<ErrorResponse> handleAllExceptions(Exception e, WebRequest request) {
    log.error("exception", e);
    ErrorResponse errorResponse = new ErrorResponse(e.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public ResponseEntity<Object> handleNumberFormatException(NumberFormatException e) {
    log.debug("handleNumberFormatException called");
    ErrorResponse errorResponse = new ErrorResponse(BadRequestException.DEFAULT_MESSAGE,
            e.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleBindException(BindException e, HttpHeaders headers,
                                                       HttpStatus status, WebRequest request) {
    log.debug("handleBindException called" + e.getFieldError().getDefaultMessage());
    ErrorResponse errorResponse
            = new ErrorResponse(BadRequestException.DEFAULT_MESSAGE,
            e.getFieldError().getDefaultMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                HttpHeaders headers,
                                                                HttpStatus status,
                                                                WebRequest request) {
    log.debug("handleMethodArgumentNotValid called");
    ErrorResponse errorResponse =
            new ErrorResponse(BadRequestException.DEFAULT_MESSAGE,
            e.getBindingResult().getFieldError().getDefaultMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException e, HttpHeaders headers,
                                                      HttpStatus status, WebRequest request) {
    log.debug("handleTypeMismatch called");
    ErrorResponse errorResponse
            = new ErrorResponse(BadRequestException.DEFAULT_MESSAGE, e.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e,
                                                                HttpHeaders headers,
                                                                HttpStatus status,
                                                                WebRequest request) {
    log.debug("handleHttpMessageNotReadable called");
    ErrorResponse errorResponse
            = new ErrorResponse(BadRequestException.DEFAULT_MESSAGE, e.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }
}