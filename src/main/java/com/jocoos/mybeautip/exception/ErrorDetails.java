package com.jocoos.mybeautip.exception;

import java.util.Date;

import lombok.Getter;

@Getter
public class ErrorDetails {

  private Date timestamp;
  private String error;
  private String details;

  private ErrorDetails(Date timestamp, String error, String details) {
    this.timestamp = timestamp;
    this.error = error;
    this.details = details;
  }

  public ErrorDetails(String error, String details) {
    this.timestamp = new Date();
    this.error = error;
    this.details = details;
  }
}
