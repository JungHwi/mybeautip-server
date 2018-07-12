package com.jocoos.mybeautip.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends MybeautipRuntimeException {

  public BadRequestException(String description) {
    super("bad_request", description);
  }

  public BadRequestException(String message, String description) {
    super(message, description);
  }
}
