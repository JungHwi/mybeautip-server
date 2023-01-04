package com.jocoos.mybeautip.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminMemberRepository extends JpaRepository<AdminMember, String> {

    Page<AdminMember> findByMemberLinkAndMemberDeletedAtIsNullOrderByStoreIdDesc(int link, Pageable pageable);

    Page<AdminMember> findByMemberLinkAndMemberDeletedAtIsNotNullOrderByStoreIdDesc(int link, Pageable pageable);

    Page<AdminMember> findByMemberLinkAndMemberDeletedAtIsNullOrderByStoreLikeCountDesc(int link, Pageable pageable);

    Page<AdminMember> findByMemberLinkAndMemberDeletedAtIsNullOrderByStoreGoodsCountDesc(int link, Pageable pageable);

    boolean existsByEmail(String email);

    Optional<AdminMember> findByMemberId(Long memberId);
}
