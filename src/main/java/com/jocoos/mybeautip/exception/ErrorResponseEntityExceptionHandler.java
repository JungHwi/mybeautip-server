package com.jocoos.mybeautip.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class ErrorResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(MemberNotFoundException.class)
  public final ResponseEntity<ErrorDetails> handleMemberNotFoundException(MemberNotFoundException e, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(e.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public final ResponseEntity<ErrorDetails> handleAllExceptions(Exception e, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(e.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
  }
}
