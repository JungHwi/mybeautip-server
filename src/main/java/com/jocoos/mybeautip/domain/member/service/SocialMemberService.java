package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.member.SocialMember;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import org.springframework.stereotype.Service;

@Service
public interface SocialMemberService<T extends SocialMember> {

    boolean exists(String socialId);

    T findById(String id);

    T save(SignupRequest request, long memberId);
}
