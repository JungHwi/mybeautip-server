package com.jocoos.mybeautip.member;

import org.springframework.stereotype.Service;

@Service
public interface SocialMember {

    String getSocialId();
    Long getMemberId();
}
