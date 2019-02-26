package com.jocoos.mybeautip.security;

import javax.transaction.Transactional;
import java.util.Map;

import com.google.common.base.Strings;
import com.jocoos.mybeautip.exception.AuthenticationException;
import com.jocoos.mybeautip.member.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

@Slf4j
public class KakaoTokenGranter extends AbstractTokenGranter {

  private final MemberRepository memberRepository;
  private final KakaoMemberRepository kakaoMemberRepository;

  public KakaoTokenGranter(
      AuthorizationServerTokenServices tokenServices,
      ClientDetailsService clientDetailsService,
      OAuth2RequestFactory requestFactory,
      MemberRepository memberRepository,
      KakaoMemberRepository kakaoMemberRepository) {
    super(tokenServices, clientDetailsService, requestFactory, "kakao");
    this.memberRepository = memberRepository;
    this.kakaoMemberRepository = kakaoMemberRepository;
  }

  @Override
  protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
    Map<String, String> requestParameters = tokenRequest.getRequestParameters();
    String kakaoId = requestParameters.get("kakao_id");
    String username = requestParameters.get("username");
    log.debug("kakao id: {}, username: {}", kakaoId, username);

    if (Strings.isNullOrEmpty(kakaoId)) {
      throw new AuthenticationException("kakao ID is required");
    }

    if (kakaoId.length() > 30) {
      throw new AuthenticationException("The kakao ID must be less or equals to 30");
    }

    return kakaoMemberRepository.findById(kakaoId)
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
    kakaoMemberRepository.save(new KakaoMember(params.get("kakao_id"), member.getId()));
    return member;
  }
}
