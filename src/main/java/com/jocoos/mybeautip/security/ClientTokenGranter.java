package com.jocoos.mybeautip.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

@Slf4j
public class ClientTokenGranter extends AbstractTokenGranter {

  public ClientTokenGranter(
      AuthorizationServerTokenServices tokenServices,
      ClientDetailsService clientDetailsService,
      OAuth2RequestFactory requestFactory) {
    super(tokenServices, clientDetailsService, requestFactory, "client");
  }

  @Override
  protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("guest", "");
    return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), authenticationToken);
  }
}
