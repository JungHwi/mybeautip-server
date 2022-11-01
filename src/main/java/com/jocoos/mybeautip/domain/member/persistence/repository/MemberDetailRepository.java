package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.jocoos.mybeautip.domain.member.persistence.domain.MemberDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberDetailRepository extends JpaRepository<MemberDetail, Long> {

    Optional<MemberDetail> findByMemberId(long memberId);
    Long countByInviterId(Long memberId);
}
