package com.jocoos.mybeautip.security;

import java.util.Map;

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
    log.debug("kakao id: {}", kakaoId);

    return kakaoMemberRepository.findById(kakaoId)
        .map(m -> generateToken(memberRepository.getOne(m.getMemberId()), client, tokenRequest))
        .orElse(generateToken(createRookie(requestParameters), client, tokenRequest));
  }

  private OAuth2Authentication generateToken(Member member, ClientDetails client, TokenRequest tokenRequest) {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getId(), "");
    return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), authenticationToken);
  }

  private Member createRookie(Map<String, String> params) {
    Member member = memberRepository.save(new Member(params));
    kakaoMemberRepository.save(new KakaoMember(params.get("kakao_id"), member.getId()));
    return member;
  }
}
