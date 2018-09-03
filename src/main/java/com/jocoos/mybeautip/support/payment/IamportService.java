package com.jocoos.mybeautip.support.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;

import com.jocoos.mybeautip.exception.MybeautipRuntimeException;

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
  private final ObjectMapper objectMapper;

  public IamportService(RestTemplate restTemplate,
                        ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public String getToken() {
    String tokenUri = UriComponentsBuilder.newInstance()
       .fromUriString(api).path("/users/getToken").toUriString();

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("imp_key", key);
    body.add("imp_secret", secret);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

    PaymentTokenResponse response = restTemplate.postForObject(tokenUri, request, PaymentTokenResponse.class);
    log.debug("{}, {}", response.getCode(), response.getMessage());
    if (response.getCode() != 0) {
      throw new MybeautipRuntimeException(response.getMessage());
    }

    return response.getResponse().getAccessToken();
  }

  @Override
  public PaymentResponse getPayment(String accessToken, String id) {
    String tokenUri = UriComponentsBuilder.newInstance()
       .fromUriString(api).path(String.format("/payments/{%s}", id)).toUriString();

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, accessToken);

    HttpEntity<Object> request = new HttpEntity<>(headers);
    ResponseEntity<PaymentResponse> responseEntity = restTemplate.exchange(tokenUri, HttpMethod.GET, request, PaymentResponse.class);
    PaymentResponse response = responseEntity.getBody();
    if (response != null) {
      log.debug("{}, {}", response.getCode(), response.getMessage());
      if (response.getCode() != 0) {
        throw new MybeautipRuntimeException(response.getMessage());
      }
    }

    return response;
  }

  @Override
  public PaymentResponse cancelPayment(String accessToken, String impUid) {
    String tokenUri = UriComponentsBuilder.newInstance()
       .fromUriString(api).path("/payments/cancel").toUriString();

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("merchant_uid", impUid);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, accessToken);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);


    PaymentResponse response = restTemplate.postForObject(tokenUri, request, PaymentResponse.class);
    log.debug("{}, {}", response.getCode(), response.getMessage());
    if (response.getCode() != 0) {
      throw new MybeautipRuntimeException(response.getMessage());
    }

    return response;
  }
}
