package com.jocoos.mybeautip.domain.event.service;

import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.member.Member;

public interface PresentService {

    void present(Member member, EventJoin eventJoin);
}
