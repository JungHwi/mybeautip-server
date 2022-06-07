package com.jocoos.mybeautip.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacebookMemberRepository extends JpaRepository<FacebookMember, String> {
    boolean existsByFacebookId(String facebookId);

    FacebookMember getByFacebookId(String facebookId);

    Optional<FacebookMember> findByMemberId(Long memberId);
}
