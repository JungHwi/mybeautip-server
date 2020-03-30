package com.jocoos.mybeautip.security;

import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.AuthenticationException;
import com.jocoos.mybeautip.member.*;

@Slf4j
public class AppleTokenGranter extends AbstractTokenGranter {

  private final MemberRepository memberRepository;
  private final AppleMemberRepository appleMemberRepository;

  public AppleTokenGranter(
      AuthorizationServerTokenServices tokenServices,
      ClientDetailsService clientDetailsService,
      OAuth2RequestFactory requestFactory,
      MemberRepository memberRepository,
      AppleMemberRepository appleMemberRepository) {
    super(tokenServices, clientDetailsService, requestFactory, "apple");
    this.memberRepository = memberRepository;
    this.appleMemberRepository = appleMemberRepository;
  }

  @Override
  protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
    Map<String, String> requestParameters = tokenRequest.getRequestParameters();
    String appleId = requestParameters.get("apple_id");
    String name = requestParameters.get("name");
    String email = requestParameters.get("email");
    log.debug("apple id: {}, email: {}, name: {}", appleId, name, email);

    if (Strings.isNullOrEmpty(appleId)) {
      throw new AuthenticationException("Apple ID is required");
    }

    return appleMemberRepository.findById(appleId)
        .map(m -> generateToken(memberRepository.getOne(m.getMemberId()), client, tokenRequest))
        .orElseGet(() -> generateToken(createRookie(requestParameters), client, tokenRequest));
  }

  private OAuth2Authentication generateToken(Member member, ClientDetails client, TokenRequest tokenRequest) {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getId(), "");
    return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), authenticationToken);
  }

  @Transactional
  private Member createRookie(Map<String, String> params) {
    Member member = memberRepository.save(new Member(params));
    appleMemberRepository.save(new AppleMember(params.get("apple_id"), params.get("email"), params.get("name"), member.getId()));
    return member;
  }
}
