package com.jocoos.mybeautip.security;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;

import com.jocoos.mybeautip.config.Oauth2Config;
import com.jocoos.mybeautip.exception.MybeautipRuntimeException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Service
public class Oauth2Client {
  private final RestTemplate restTemplate;
  private Oauth2Config.Oauth2ProviderConfig providerConfig = null;

  public void setProviderConfig(Oauth2Config.Oauth2ProviderConfig providerConfig) {
    log.debug("{}", providerConfig);
    this.providerConfig = providerConfig;
  }

  public String getAccessToken(String code) {
    if (providerConfig == null) {
      throw new MybeautipRuntimeException("Provider config required");
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("grant_type", providerConfig.getAuthorizationGrantType());
    body.add("client_id", providerConfig.getClientId());
    body.add("redirect_uri", providerConfig.getRedirectUri());
    body.add("code", code);

    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
    log.debug("{}", request);

    ResponseEntity<AccessTokenResponse> response = restTemplate
        .postForEntity(providerConfig.getTokenUri(), request, AccessTokenResponse.class);

    log.debug("{}, {}", response.getHeaders(), response.getBody());
    return response.getBody().getAccessToken();
  }

  public HashMap<String, Object> getUserData(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.set("Authorization", String.format("Bearer %s", accessToken));

    MultiValueMap<String, String> request = new LinkedMultiValueMap<>();

    HttpEntity httpEntity = new HttpEntity<>(request, headers);
    ParameterizedTypeReference<HashMap<String, Object>> responseType =
        new ParameterizedTypeReference<HashMap<String, Object>>() {
      };

    ResponseEntity<HashMap<String, Object>> responseEntity = restTemplate
        .exchange(providerConfig.getUserInfoUri(), HttpMethod.GET, httpEntity, responseType);

    HashMap<String, Object> body = responseEntity.getBody();
    log.debug("body: {}", body);
    return body;
  }
}
