package com.jocoos.mybeautip.security;

import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.admin.AdminMemberRepository;
import com.jocoos.mybeautip.exception.AuthenticationException;
import com.jocoos.mybeautip.member.KakaoMember;
import com.jocoos.mybeautip.member.KakaoMemberRepository;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;

@Slf4j
public class AdminTokenGranter extends AbstractTokenGranter {

  private final MemberRepository memberRepository;
  private final AdminMemberRepository adminMemberRepository;
  private final PasswordEncoder passwordEncoder;

  public AdminTokenGranter(
      AuthorizationServerTokenServices tokenServices,
      ClientDetailsService clientDetailsService,
      OAuth2RequestFactory requestFactory,
      MemberRepository memberRepository,
      AdminMemberRepository adminMemberRepository,
      PasswordEncoder passwordEncoder) {
    super(tokenServices, clientDetailsService, requestFactory, "admin");
    this.memberRepository = memberRepository;
    this.adminMemberRepository = adminMemberRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
    Map<String, String> requestParameters = tokenRequest.getRequestParameters();
    String adminId = requestParameters.get("admin_id");
    String password = requestParameters.get("password");

    log.debug("admin id: {}, password: {}", adminId, password);

    if (Strings.isNullOrEmpty(adminId)) {
      throw new AuthenticationException("admin ID is required");
    }

    if (adminId.length() > 30) {
      throw new AuthenticationException("The admin ID must be less or equals to 30");
    }

    if (Strings.isNullOrEmpty(password)) {
      throw new AuthenticationException("admin password is required");
    }

    return adminMemberRepository.findById(adminId)
       .filter(m -> passwordEncoder.matches(password, m.getPassword()))
       .map(m -> generateToken(memberRepository.getOne(m.getMemberId()), client, tokenRequest))
       .orElseThrow(() -> new AuthenticationException("admin ID or password Invalid."));
  }

  private OAuth2Authentication generateToken(Member member, ClientDetails client, TokenRequest tokenRequest) {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getId(), "");
    return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), authenticationToken);
  }
}
