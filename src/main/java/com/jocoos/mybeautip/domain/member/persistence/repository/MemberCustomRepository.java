package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;

import java.util.Map;

public interface MemberCustomRepository {
    Map<MemberStatus, Long> getStatusesWithCount();
}
