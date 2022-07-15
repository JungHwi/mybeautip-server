package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.jocoos.mybeautip.domain.member.persistence.domain.MemberInvitationInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberInvitationInfoRepository extends JpaRepository<MemberInvitationInfo, Long> {
}
