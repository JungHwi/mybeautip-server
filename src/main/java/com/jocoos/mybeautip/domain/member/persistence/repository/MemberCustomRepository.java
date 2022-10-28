package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.jocoos.mybeautip.domain.member.dto.MemberStatusResponse;

import java.util.List;

public interface MemberCustomRepository {
    List<MemberStatusResponse> getStatusesWithCount();
}
