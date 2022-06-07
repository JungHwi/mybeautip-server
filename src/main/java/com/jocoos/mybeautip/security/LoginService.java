package com.jocoos.mybeautip.security;

public interface LoginService {
    SocialMemberRequest getMember(String code, String state);
}
