package com.jocoos.mybeautip.security;

public interface LoginService {
  SocialMember getMember(String code, String state);
}
