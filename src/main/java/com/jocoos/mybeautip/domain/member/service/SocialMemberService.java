package com.jocoos.mybeautip.domain.member.service;

import org.springframework.stereotype.Service;

@Service
public interface SocialMemberService {

    boolean exists(String socialId);
}
