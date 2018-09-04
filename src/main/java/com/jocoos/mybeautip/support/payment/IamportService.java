package com.jocoos.mybeautip.support.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MybeautipRuntimeException;
import com.jocoos.mybeautip.exception.NotFoundException;

@Slf4j
@Service
public class IamportService implements IamportApi {

  @Value("${mybeautip.iamport.api}")
  private String api;


  @Value("${mybeautip.iamport.key}")
  private String key;

  @Value("${mybeautip.iamport.secret}")
  private String secret;

  private final RestTemplate restTemplate;

  public IamportService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public String getToken() {
    String tokenUri = UriComponentsBuilder.newInstance()
       .fromUriString(api).path("/users/getToken").toUriString();

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("imp_key", key);
    body.add("imp_secret", secret);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

    PaymentTokenResponse response = restTemplate.postForObject(tokenUri, request, PaymentTokenResponse.class);
    log.debug("{}, {}", response.getCode(), response.getResponse());
    if (response.getCode() != 0) {
      throw new MybeautipRuntimeException(response.getMessage());
    }

    return response.getResponse().getAccessToken();
  }

  @Override
  public PaymentResponse getPayment(String accessToken, String id) {
    String tokenUri = UriComponentsBuilder.newInstance()
       .fromUriString(api).path(String.format("/payments/%s", id)).toUriString();

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, accessToken);

    HttpEntity<Object> request = new HttpEntity<>(headers);
    ResponseEntity<PaymentResponse> responseEntity = null;
    try {
      responseEntity = restTemplate.exchange(tokenUri, HttpMethod.GET, request, PaymentResponse.class);
      PaymentResponse response = responseEntity.getBody();
      if (response != null) {
        log.debug("{}, {}", response.getCode(), response.getResponse());
        if (response.getCode() != 0) {
          throw new BadRequestException("invalid_payment_request", response.getMessage());
        }
      }

      return response;
    } catch (HttpClientErrorException e) {
      log.error("Get payment error", e);
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("payment_id_not_found", "invalid payment id");
      }
      // TODO: Catch more error cases
    }

    return null;
  }

  @Override
  public PaymentResponse cancelPayment(String accessToken, String impUid) {
    String tokenUri = UriComponentsBuilder.newInstance()
       .fromUriString(api).path("/payments/cancel").toUriString();

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("imp_uid", impUid);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, accessToken);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

    PaymentResponse response = restTemplate.postForObject(tokenUri, request, PaymentResponse.class);
    log.debug("{}, {}", response.getCode(), response.getResponse());
    if (response.getCode() != 0) {
      throw new BadRequestException("invalid_payment_request", response.getMessage());
    }

    return response;
  }
}
