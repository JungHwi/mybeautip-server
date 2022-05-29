package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.exception.AuthenticationException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.AppleMemberRepository;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.Map;

@Slf4j
public class AppleTokenGranter extends AbstractTokenGranter {

  private final MemberService memberService;
  private final MemberRepository memberRepository;
  private final AppleMemberRepository appleMemberRepository;

  public AppleTokenGranter(
      AuthorizationServerTokenServices tokenServices,
      ClientDetailsService clientDetailsService,
      OAuth2RequestFactory requestFactory,
      MemberService memberService,
      MemberRepository memberRepository,
      AppleMemberRepository appleMemberRepository) {
    super(tokenServices, clientDetailsService, requestFactory, "apple");
    this.memberService = memberService;
    this.memberRepository = memberRepository;
    this.appleMemberRepository = appleMemberRepository;
  }

  @Override
  protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
    Map<String, String> requestParameters = tokenRequest.getRequestParameters();
    String appleId = requestParameters.get("social_id");
    String name = requestParameters.get("name");
    String email = requestParameters.get("email");
    log.debug("apple id: {}, email: {}, name: {}", appleId, name, email);

    if (StringUtils.isBlank(appleId)) {
      throw new AuthenticationException("Apple ID is required");
    }

    return appleMemberRepository.findById(appleId)
            .map(m -> generateToken(memberRepository.getById(m.getMemberId()), client, tokenRequest))
            .orElseThrow(() -> new MemberNotFoundException("No such apple user. apple id - " + appleId));
  }

  private OAuth2Authentication generateToken(Member member, ClientDetails client, TokenRequest tokenRequest) {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getId(), "");
    return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), authenticationToken);
  }
}
