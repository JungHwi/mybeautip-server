package com.jocoos.mybeautip.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {

  private String error;
  private String errorDescription;

  public ErrorResponse(String error, String errorDescription) {
    this.error = error;
    this.errorDescription = errorDescription;
  }
}
