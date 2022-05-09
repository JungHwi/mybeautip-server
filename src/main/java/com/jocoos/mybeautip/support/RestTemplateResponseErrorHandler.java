package com.jocoos.mybeautip.support;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    log.debug("response code : {}", response.getStatusCode());
    return HttpStatus.INTERNAL_SERVER_ERROR == response.getStatusCode();
  }

  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    log.debug("handleError: {}", response);
  }
}
