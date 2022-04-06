package com.jocoos.mybeautip.security;

import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.AuthenticationException;
import com.jocoos.mybeautip.member.FacebookMember;
import com.jocoos.mybeautip.member.FacebookMemberRepository;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;

@Slf4j
public class FacebookTokenGranter extends AbstractTokenGranter {

  private final MemberRepository memberRepository;
  private final FacebookMemberRepository facebookMemberRepository;

  public FacebookTokenGranter(
      AuthorizationServerTokenServices tokenServices,
      ClientDetailsService clientDetailsService,
      OAuth2RequestFactory requestFactory,
      MemberRepository memberRepository,
      FacebookMemberRepository facebookMemberRepository) {
    super(tokenServices, clientDetailsService, requestFactory, "facebook");
    this.memberRepository = memberRepository;
    this.facebookMemberRepository = facebookMemberRepository;
  }

  @Override
  protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
    Map<String, String> requestParameters = tokenRequest.getRequestParameters();
    String facebookId = requestParameters.get("facebook_id");
    String username = requestParameters.get("facebook_id");
    log.debug("facebook id: {}, username: {}", facebookId, username);

    if (StringUtils.isBlank(facebookId)) {
      throw new AuthenticationException("facebook ID is required");
    }

    if (facebookId.length() > 20) {
      throw new AuthenticationException("The facebook ID must be less or equals to 20");
    }

    return facebookMemberRepository.findById(facebookId)
        .map(m -> generateToken(memberRepository.getById(m.getMemberId()), client, tokenRequest))
        .orElseGet(() -> generateToken(createRookie(requestParameters), client, tokenRequest));
  }

  private OAuth2Authentication generateToken(Member member, ClientDetails client, TokenRequest tokenRequest) {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getId(), "");
    return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), authenticationToken);
  }

  @Transactional
  private Member createRookie(Map<String, String> params) {
    Member member1 = new Member(params);
    log.debug("member1: {}", member1);

    Member member = memberRepository.save(new Member(params));
    facebookMemberRepository.save(new FacebookMember(params.get("facebook_id"), member.getId()));
    return member;
  }
}
