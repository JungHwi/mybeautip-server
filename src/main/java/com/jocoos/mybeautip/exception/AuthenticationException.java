package com.jocoos.mybeautip.exception;

import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;

public class AuthenticationException extends ClientAuthenticationException {

  public AuthenticationException(String msg) {
    super(msg);
  }

  public AuthenticationException(String msg, Throwable t) {
    super(msg, t);
  }

  @Override
  public String getOAuth2ErrorCode() {
    return "mybeautip_oauth_error";
  }
}
