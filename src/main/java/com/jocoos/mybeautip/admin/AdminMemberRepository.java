package com.jocoos.mybeautip.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminMemberRepository extends JpaRepository<AdminMember, String> {

    boolean existsByEmail(String email);

    Optional<AdminMember> findByMemberId(Long memberId);
}
