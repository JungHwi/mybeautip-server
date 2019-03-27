package com.jocoos.mybeautip.support.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.PaymentConflictException;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.restapi.AccountController;
import com.jocoos.mybeautip.support.slack.SlackService;

@Slf4j
@Service
public class IamportService implements IamportApi {
  
  private final SlackService slackService;
  private final MessageService messageService;

  @Value("${mybeautip.iamport.api}")
  private String api;


  @Value("${mybeautip.iamport.key}")
  private String key;

  @Value("${mybeautip.iamport.secret}")
  private String secret;

  private final RestTemplate restTemplate;

  public IamportService(SlackService slackService,
                        MessageService messageService,
                        RestTemplate restTemplate) {
    this.slackService = slackService;
    this.messageService = messageService;
    this.restTemplate = restTemplate;
  }

  @Override
  public String getToken() {
    String tokenUri = fromUriString(api).path("/users/getToken").toUriString();

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("imp_key", key);
    body.add("imp_secret", secret);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

    PaymentTokenResponse response;
    try {
      response = restTemplate.postForObject(tokenUri, request, PaymentTokenResponse.class);
    } catch (RestClientException e) {
      log.error("invalid_iamport_response, GetToken failed");
      slackService.sendForImportGetTokenFail();
      throw new PaymentConflictException();
    }
    
    if (response == null || response.getCode() != 0) {
      log.warn("invalid_iamport_response, GetToken failed");
      slackService.sendForImportGetTokenFail();
      throw new PaymentConflictException();
    }
    
    log.debug("{}, {}", response.getCode(), response.getResponse());
    return response.getResponse().getAccessToken();
  }

  @Override
  public PaymentResponse getPayment(String accessToken, String id) {
    String tokenUri = fromUriString(api).path(String.format("/payments/%s", id)).toUriString();

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, accessToken);

    HttpEntity<Object> request = new HttpEntity<>(headers);
    ResponseEntity<PaymentResponse> responseEntity;
    try {
      responseEntity = restTemplate.exchange(tokenUri, HttpMethod.GET, request, PaymentResponse.class);
      log.info("iamport get payment response: " + responseEntity.getBody());
      return responseEntity.getBody();
    } catch (RestClientException e) {
      log.error("invalid_iamport_response: Get payment error", e);
      slackService.sendForImportGetPaymentException(id, e.getMessage());
      throw new PaymentConflictException();
    }
  }

  @Override
  public PaymentResponse cancelPayment(String accessToken, String impUid) {
    String tokenUri = fromUriString(api).path("/payments/cancel").toUriString();

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("imp_uid", impUid);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, accessToken);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

    PaymentResponse response;
    try {
       response = restTemplate.postForObject(tokenUri, request, PaymentResponse.class);
    } catch (RestClientException e) {
      log.error("invalid_iamport_response, Check payment status, payment_id: " + impUid);
      slackService.sendForImportCancelPaymentException(impUid, e.getMessage());
      throw new PaymentConflictException();
    }
    
    if (response == null || response.getCode() != 0) {
      log.warn("invalid_iamport_response, Check payment status, payment_id: " + impUid);
      slackService.sendForImportCancelPaymentException(impUid, (response == null) ? "" : response.toString());
      throw new PaymentConflictException();
    }

    log.debug("{}, {}", response.getCode(), response.getResponse());
    return response;
  }
  
  public ResponseEntity<VbankResponse> validAccountInfo(AccountController.UpdateAccountInfo info, String lang) {
    String ACCOUNT_INVALID_INFO = "account.invalid_info";
    
    String accessToken = getToken();
    String requestUri = fromUriString(api).path("/vbanks/holder")
        .queryParam("bank_code", info.getBankCode())
        .queryParam("bank_num", info.getBankAccount())
        .toUriString();
  
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, accessToken);

    HttpEntity<Object> request = new HttpEntity<>(headers);

    try {
      return restTemplate.exchange(requestUri, HttpMethod.GET, request, VbankResponse.class);
    } catch (HttpClientErrorException httpClientErrorException) {
      if (httpClientErrorException.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new BadRequestException("invalid_account", messageService.getMessage(ACCOUNT_INVALID_INFO, lang));
      }
      log.warn("account_info_validation_iamport_fail", httpClientErrorException.getMessage());
      return null;  // Do not throw exception
    } catch (RestClientException restClientException) {
      log.warn("account_info_validation_iamport_fail", restClientException.getMessage());
      return null;  // Do not throw exception
    }
  }
}
