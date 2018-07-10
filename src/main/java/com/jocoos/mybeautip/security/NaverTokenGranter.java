package com.jocoos.mybeautip.security;

import java.util.Map;

import com.google.common.base.Strings;
import com.jocoos.mybeautip.exception.InvalidRequestException;
import com.jocoos.mybeautip.member.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

@Slf4j
public class NaverTokenGranter extends AbstractTokenGranter {

  private final MemberRepository memberRepository;
  private final NaverMemberRepository naverMemberRepository;

  public NaverTokenGranter(
      AuthorizationServerTokenServices tokenServices,
      ClientDetailsService clientDetailsService,
      OAuth2RequestFactory requestFactory,
      MemberRepository memberRepository,
      NaverMemberRepository naverMemberRepository) {
    super(tokenServices, clientDetailsService, requestFactory, "naver");
    this.memberRepository = memberRepository;
    this.naverMemberRepository = naverMemberRepository;
  }

  @Override
  protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
    Map<String, String> requestParameters = tokenRequest.getRequestParameters();
    String naverId = requestParameters.get("naver_id");
    String username = requestParameters.get("naver_id");

    log.debug("naver id: {}, username: {}", naverId, username);

    if (Strings.isNullOrEmpty(naverId)) {
      throw new InvalidRequestException("naver ID is required");
    }

    if (Strings.isNullOrEmpty(username)) {
      throw new InvalidRequestException("username is required");
    }

    return naverMemberRepository.findById(naverId)
        .map(m -> generateToken(memberRepository.getOne(m.getMemberId()), client, tokenRequest))
        .orElseGet(() -> generateToken(createRookie(requestParameters), client, tokenRequest));
  }

  private OAuth2Authentication generateToken(Member member, ClientDetails client, TokenRequest tokenRequest) {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getId(), "");
    return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), authenticationToken);
  }

  private Member createRookie(Map<String, String> params) {
    Member member = memberRepository.save(new Member(params));
    naverMemberRepository.save(new NaverMember(params, member.getId()));
    return member;
  }

  
}
