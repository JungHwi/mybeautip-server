package com.jocoos.mybeautip.exception;

public class AccessDeniedException extends MybeautipRuntimeException {

  public AccessDeniedException(String description) {
    super("access denied", description);
  }
}
