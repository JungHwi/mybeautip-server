package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.exception.AuthenticationException;
import com.jocoos.mybeautip.member.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
public class NaverTokenGranter extends AbstractTokenGranter {

  private final MemberService memberService;
  private final MemberRepository memberRepository;
  private final NaverMemberRepository naverMemberRepository;

  public NaverTokenGranter(
      AuthorizationServerTokenServices tokenServices,
      ClientDetailsService clientDetailsService,
      OAuth2RequestFactory requestFactory,
      MemberService memberService,
      MemberRepository memberRepository,
      NaverMemberRepository naverMemberRepository) {

    super(tokenServices, clientDetailsService, requestFactory, "naver");
    this.memberService = memberService;
    this.memberRepository = memberRepository;
    this.naverMemberRepository = naverMemberRepository;
  }

  @Override
  protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
    Map<String, String> requestParameters = tokenRequest.getRequestParameters();
    String naverId = requestParameters.get("naver_id");
    String username = requestParameters.get("naver_id");

    log.debug("naver id: {}, username: {}", naverId, username);

    if (StringUtils.isBlank(naverId)) {
      throw new AuthenticationException("naver ID is required");
    }

    if (naverId.length() > 30) {
      throw new AuthenticationException("The naver ID must be less or equals to 30");
    }

    return naverMemberRepository.findById(naverId)
        .map(m -> generateToken(memberRepository.getById(m.getMemberId()), client, tokenRequest))
        .orElseGet(() -> generateToken(createRookie(requestParameters), client, tokenRequest));
  }

  private OAuth2Authentication generateToken(Member member, ClientDetails client, TokenRequest tokenRequest) {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getId(), "");
    return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), authenticationToken);
  }
  
  @Transactional
  public Member createRookie(Map<String, String> params) {
    Member member = memberService.register(params);
    naverMemberRepository.save(new NaverMember(params, member.getId()));
    return member;
  }
}
