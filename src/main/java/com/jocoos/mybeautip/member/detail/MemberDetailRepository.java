package com.jocoos.mybeautip.member.detail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberDetailRepository extends JpaRepository<MemberDetail, Long> {

    Optional<MemberDetail> findByMemberId(long memberId);
}
