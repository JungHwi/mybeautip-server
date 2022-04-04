package com.jocoos.mybeautip.security;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.HashMap;

import com.jocoos.mybeautip.config.Oauth2Config;
import com.jocoos.mybeautip.exception.MybeautipRuntimeException;

import com.google.common.base.Strings;
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

  public String getAccessToken(String code, String state, String redirectUri) {
    if (providerConfig == null) {
      throw new MybeautipRuntimeException("Provider config required");
    }

    if ("GET".equals(providerConfig.getTokenMethod())) {
      return accessTokenWithGet(code, state, redirectUri);
    }
    return accessTokenWithPost(code, state, redirectUri);
  }

  private String accessTokenWithGet(String code, String state, String redirectUri) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(headers);
    log.debug("{}", entity);

    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(providerConfig.getTokenUri())
        .queryParam("grant_type", providerConfig.getAuthorizationGrantType())
        .queryParam("client_id", providerConfig.getClientId())
        .queryParam("redirect_uri", redirectUri)
        .queryParam("code", code);

    if (!Strings.isNullOrEmpty(providerConfig.getClientSecret())) {
      uriBuilder.queryParam("client_secret", providerConfig.getClientSecret());
    }

    if (!Strings.isNullOrEmpty(state)) {
      uriBuilder.queryParam("state", state);
    }

    String uri = uriBuilder.toUriString();
    log.debug("{}", uri);
    ResponseEntity<AccessTokenResponse> response = restTemplate
        .exchange(uri, HttpMethod.GET, entity, AccessTokenResponse.class);

    log.debug("{}", response.getHeaders());
    log.debug("{}", response.getBody());
    return response.getBody().getAccessToken();
  }

  private String accessTokenWithPost(String code, String state, String redirectUri) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("grant_type", providerConfig.getAuthorizationGrantType());
    body.add("client_id", providerConfig.getClientId());
    body.add("redirect_uri", redirectUri);
    if (!Strings.isNullOrEmpty(providerConfig.getClientSecret())) {
      body.add("client_secret", providerConfig.getClientSecret());
    }
    if (!Strings.isNullOrEmpty(state)) {
      body.add("state", state);
    }

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
    headers.set("Authorization", String.format("Bearer %s", accessToken));

    HttpEntity httpEntity = new HttpEntity<>(headers);
    ParameterizedTypeReference<HashMap<String, Object>> responseType =
        new ParameterizedTypeReference<HashMap<String, Object>>() {
      };

    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(providerConfig.getUserInfoUri());
    if (!Strings.isNullOrEmpty(providerConfig.getFields())) {
      uriBuilder.queryParam("fields", providerConfig.getFields());
    }

    String uri = uriBuilder.toUriString();
    log.debug("{}", uri);
    ResponseEntity<HashMap<String, Object>> responseEntity = restTemplate
        .exchange(uri, HttpMethod.GET, httpEntity, responseType);

    HashMap<String, Object> body = responseEntity.getBody();
    log.debug("body: {}", body);
    return body;
  }
}
